package fete.be.domain.event.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import fete.be.domain.event.application.dto.ParticipantDto;
import fete.be.domain.event.persistence.Participant;
import fete.be.domain.event.persistence.ParticipantRepository;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QRCodeService {

    private final ObjectMapper objectMapper;
    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final PosterService posterService;

    public String generateQRCodeBase64(Participant obj, int width, int height) throws Exception {
        // Participant 객체가 엔티티이기 때문에 DTO로 만들어서 QR 코드 생성
        ParticipantDto participantDto = new ParticipantDto(obj.getParticipantId(), obj.getMember().getMemberId(), obj.getEvent().getEventId(), obj.getPayment().getPaymentId());

        // 보낼 객체를 json 문자열로 변환
        String jsonString = objectMapper.writeValueAsString(participantDto);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(jsonString, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(pngData);
    }

    /**
     * QR 코드 검증 API
     */
    public Long verifyQRCode(MultipartFile file, Long posterId) throws IOException, NotFoundException {
        // QR 코드 이미지 파일을 읽기
        InputStream inputStream = file.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        String base64Image = result.getText();


        // Base64 디코딩하여 BufferedImage로 변환
        bufferedImage = decodeQRCodeImage(base64Image);

        // QR 코드 이미지에서 QR 코드 텍스트 추출
        source = new BufferedImageLuminanceSource(bufferedImage);
        bitmap = new BinaryBitmap(new HybridBinarizer(source));

        result = new MultiFormatReader().decode(bitmap);
        String qrCodeText = result.getText();

        // 역직렬화
        ParticipantDto participantDto = objectMapper.readValue(qrCodeText, ParticipantDto.class);


        // QR 코드 이미지 파일을 읽기
//        InputStream inputStream = file.getInputStream();
//        BufferedImage bufferedImage = ImageIO.read(inputStream);
//
//        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Result result = new MultiFormatReader().decode(bitmap);
//        String qrCodeText = result.getText();
//        log.info("QR_STRING={}", qrCodeText);  // 여기까진 QR 코드가 원본과 같은 값으로 도착함
//
//        // 받은 json 문자열을 ParticipantDto 객체로 역직렬화
//        ParticipantDto participantDto;
//        try {
//            participantDto = objectMapper.readValue(qrCodeText, ParticipantDto.class);
//            log.info("QR_mapper START");
//            log.info("QR_mapper={}", participantDto.getParticipantId());
//        } catch (JsonProcessingException e) {
//            log.error("Failed to deserialize QR code text to ParticipantDto: {}", e.getMessage());
//            throw new IllegalArgumentException("Invalid QR code format");
//        }
//
//        ParticipantDto participantDto = objectMapper.readValue(qrCodeText, ParticipantDto.class);
//        log.info("QR_mapper START");
//        log.info("QR_mapper={}", participantDto.getParticipantId());

        // DB에서 원본 데이터를 조회
        Participant originalParticipant = participantRepository.findById(participantDto.getParticipantId()).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage()));

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 해당 QR 코드가 올바른 이벤트 장소에서 대조하고 있는지 확인
        if (!poster.getEvent().getEventId().equals(participantDto.getEventId())) {
            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
        }

        // QR 데이터와 원본 데이터 비교
        if (!isValid(participantDto, originalParticipant)) {
            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
        }

        // 정상 로직일 경우
        return participantDto.getParticipantId();
    }

    private boolean isValid(ParticipantDto compare, Participant original) {
        // 유저 일치 확인
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.findMemberByEmail(email);
        if (!original.getMember().equals(member)) {
            return false;
        }

        // 유저가 참여한 이벤트 비교
        if (!compare.getEventId().equals(original.getEvent().getEventId())) {
            return false;
        }

        // 결제 상태 비교
        if (!compare.getPaymentId().equals(original.getPayment().getPaymentId())) {
            return false;
        }

        return true;
    }

    public static void saveQRCodeImage(String base64EncodedImage, String filePath) throws Exception {
        // Base64 인코딩된 문자열을 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedImage);

        // 디코딩된 바이트 배열을 파일로 저장
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(decodedBytes);
        }
    }

    public BufferedImage decodeQRCodeImage(String base64Image) throws IOException {
        // Base64 디코딩
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // ByteArrayInputStream을 이용해 BufferedImage로 변환
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);
        bis.close();

        return bufferedImage;
    }

//    public Long verifyQRCode(String base64Image, Long posterId) throws IOException, NotFoundException {
//        // Base64 디코딩하여 BufferedImage로 변환
//        BufferedImage bufferedImage = decodeQRCodeImage(base64Image);
//
//        // QR 코드 이미지에서 QR 코드 텍스트 추출
//        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Result result = new MultiFormatReader().decode(bitmap);
//        String qrCodeText = result.getText();
//
//        // 여기서 qrCodeText를 이용해 추가 검증 로직을 수행
//        // ...
//
//        return participantDto.getParticipantId();
//    }
}
