package fete.be.domain.image.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import fete.be.domain.image.exception.*;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final AmazonS3 amazonS3;
    private Set<String> uploadedFileNames = new HashSet<>();
    @Value("${aws.s3.bucket-name}")
    private String bucket;


    /**
     * 여러 장의 이미지를 S3에 업로드 하여 URL 리스트 반환
     */
    public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
        // 반환할 이미지 링크 주소 리스트
        List<String> uploadedUrls = new ArrayList<>();

        // 여러 이미지 업로드
        for (MultipartFile multipartFile : multipartFiles) {
            // 업로드할 이미지의 중복 검사
            if (isDuplicate(multipartFile)) {
                throw new DuplicateImageException(ResponseMessage.IMAGE_DUPLICATE.getMessage());
            }

            // 이미지 1개 업로드 하여, 반환된 URL 리스트에 추가
            String uploadedUrl = uploadFile(multipartFile, "images");
            uploadedUrls.add(uploadedUrl);
        }

        uploadedFileNames.clear();
        return uploadedUrls;
    }

    /**
     * 이미지 1장 업로드 하여 URL 반환
     */
    public String uploadFile(MultipartFile file, String dirName) {
        // 파일 이름 생성
        String randomFileName = generateRandomFileName(file, dirName);

        // 메타 데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            // 이미지 S3에 업로드 (공개 접근 가능하도록 ACL 설정 추가)
            amazonS3.putObject(bucket, randomFileName, file.getInputStream(), metadata);

        } catch (AmazonS3Exception e) {
            throw new AwsS3Exception(ResponseMessage.S3_UPLOAD_FAIL.getMessage());
        } catch (SdkClientException e) {
            throw new AwsSdkException(ResponseMessage.AWS_SDK_ERROR.getMessage());
        } catch (IOException e) {
            throw new UploadErrorException(ResponseMessage.S3_UPLOAD_FAIL.getMessage());
        }

        return amazonS3.getUrl(bucket, randomFileName).toString();
    }

    /**
     * 여러 장의 이미지를 삭제
     * @param fileUrls
     * @throws URISyntaxException
     */
    public void deleteFiles(List<String> fileUrls) throws URISyntaxException {
        // 여러 이미지 삭제
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    /**
     * 이미지 1장 삭제
     */
    public void deleteFile(String fileUrl) throws URISyntaxException {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        URI uri = new URI(fileUrl);
        String key = uri.getPath().substring(1);

        // 파일이 S3에 존재할 경우
        if (amazonS3.doesObjectExist(bucket, key)) {
            amazonS3.deleteObject(bucket, key);
        } else {  // 존재하지 않을 경우
            throw new NotFoundFileInS3Exception(ResponseMessage.S3_FILE_NO_EXIST.getMessage());
        }
    }

    /**
     * 업로드할 이미지의 중복 검사
     */
    private boolean isDuplicate(MultipartFile file) {
        try {
            // 파일의 해시값
            String fileHash = DigestUtils.md5DigestAsHex(file.getBytes());
            if (uploadedFileNames.contains(fileHash)) {
                return true;
            }
            uploadedFileNames.add(fileHash);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return false;
    }

    /**
     * 파일명 랜덤 생성
     */
    private String generateRandomFileName(MultipartFile file, String dirName) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFileName);
        String randomFileName = dirName + "/" + UUID.randomUUID() + "_" + originalFileName;
        return randomFileName;
    }

    /**
     * 파일 확장자 검사
     */
    private String validateFileExtension(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        List<String> possibleExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

        // 가능한 확장자인지 검사
        if (!possibleExtensions.contains(fileExtension)) {
            throw new InvalidExtension(ResponseMessage.INVALID_EXTENSION.getMessage());
        }
        return fileExtension;
    }
}
