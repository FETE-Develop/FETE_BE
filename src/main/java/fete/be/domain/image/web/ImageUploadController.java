package fete.be.domain.image.web;

import fete.be.domain.image.application.ImageUploadService;
import fete.be.domain.image.application.dto.UploadFilesResponse;
import fete.be.domain.image.exception.*;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;


    @PostMapping("/upload")
    public ApiResponse<UploadFilesResponse> uploadImages(@RequestParam("file") List<MultipartFile> files) {
        log.info("UploadImages API");
        Logging.time();

        try {
            // 이미지들을 S3에 업로드
            List<String> imageUrls = imageUploadService.uploadFiles(files);
            UploadFilesResponse result = new UploadFilesResponse(imageUrls);

            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_SUCCESS.getCode(), ResponseMessage.S3_UPLOAD_SUCCESS.getMessage(), result);
        } catch (AwsS3Exception e) {
            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_FAIL.getCode(), e.getMessage());
        } catch (AwsSdkException e) {
            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_FAIL.getCode(), e.getMessage());
        } catch (DuplicateImageException e) {
            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_FAIL.getCode(), e.getMessage());
        } catch (InvalidExtension e) {
            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_FAIL.getCode(), e.getMessage());
        } catch (UploadErrorException e) {
            return new ApiResponse<>(ResponseMessage.S3_UPLOAD_FAIL.getCode(), e.getMessage());
        }
    }
}
