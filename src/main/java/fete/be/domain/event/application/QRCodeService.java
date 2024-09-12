package fete.be.domain.event.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fete.be.domain.event.application.dto.request.ParticipantDto;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QRCodeService {

    private final ObjectMapper objectMapper;
    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final PosterService posterService;

    /**
     * QR 코드 발급
     */
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
     * QR 코드 검증 : 프론트에서 QR 코드 해석한 정보를 검증해주는 방식
     * 프론트에서 보내주는 바디 예시
     * {
     *      participantId: 2,
     *      memberId: 2,
     *      eventId: 1,
     *      paymentId: 1,
     * }
     */
    @Transactional
    public Long verifyQRCode(Long posterId, ParticipantDto participantDto) {
        // DB에서 원본 데이터를 조회
        Participant originalParticipant = participantRepository.findById(participantDto.getParticipantId()).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage()));

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 검증 로직
        // 해당 QR 코드가 사용된 적 있는지 확인
        if (originalParticipant.getIsParticipated()) {
            throw new IllegalArgumentException(ResponseMessage.EVENT_QR_ALREADY_USED.getMessage());
        }

        // 해당 QR 코드가 올바른 이벤트 장소에서 대조하고 있는지 확인
        if (!poster.getEvent().getEventId().equals(participantDto.getEventId())) {
            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
        }

        // QR 데이터와 원본 데이터 비교
        if (!isValid(participantDto, originalParticipant)) {
            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
        }

        // 정상 로직일 경우, 이벤트 참여 완료 처리
        Participant.completeParticipant(originalParticipant);
        return participantDto.getParticipantId();
    }

//
//    /**
//     * QR 코드 검증 : QR 코드 이미지 자체를 받아서 검증하는 방식
//     */
//    @Transactional
//    public Long verifyQRCode(MultipartFile file, Long posterId) throws IOException, NotFoundException {
//        // QR 코드 이미지 파일을 읽기
//        InputStream inputStream = file.getInputStream();
//        BufferedImage bufferedImage = ImageIO.read(inputStream);
//
//        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Result result = new MultiFormatReader().decode(bitmap);
//        String base64Image = result.getText();
//
//        // Base64 디코딩하여 BufferedImage로 변환
//        bufferedImage = decodeQRCodeImage(base64Image);
//
//        // QR 코드 이미지에서 QR 코드 텍스트 추출
//        source = new BufferedImageLuminanceSource(bufferedImage);
//        bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        result = new MultiFormatReader().decode(bitmap);
//        String qrCodeText = result.getText();
//
//        // 역직렬화
//        ParticipantDto participantDto = objectMapper.readValue(qrCodeText, ParticipantDto.class);
//
//        // DB에서 원본 데이터를 조회
//        Participant originalParticipant = participantRepository.findById(participantDto.getParticipantId()).orElseThrow(
//                () -> new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage()));
//
//        // posterId로 포스터 찾기
//        Poster poster = posterService.findPosterByPosterId(posterId);
//
//        // 검증 로직
//        // 해당 QR 코드가 사용된 적 있는지 확인
//        if (originalParticipant.getIsParticipated()) {
//            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
//        }
//
//        // 해당 QR 코드가 올바른 이벤트 장소에서 대조하고 있는지 확인
//        if (!poster.getEvent().getEventId().equals(participantDto.getEventId())) {
//            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
//        }
//
//        // QR 데이터와 원본 데이터 비교
//        if (!isValid(participantDto, originalParticipant)) {
//            throw new IllegalArgumentException(ResponseMessage.EVENT_INVALID_QR.getMessage());
//        }
//
//        // 정상 로직일 경우, 이벤트 참여 완료 처리
//        Participant.completeParticipant(originalParticipant);
//        return participantDto.getParticipantId();
//    }

    private boolean isValid(ParticipantDto compare, Participant original) {
        // 유저 일치 확인
        Member member = memberService.findMemberByEmail();
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
}
