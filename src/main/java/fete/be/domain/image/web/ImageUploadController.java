package fete.be.domain.image.web;

import fete.be.domain.image.application.ImageUploadService;
import fete.be.domain.image.application.dto.DeleteImagesRequest;
import fete.be.domain.image.application.dto.UploadFilesResponse;
import fete.be.domain.image.exception.*;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;


    /**
     * 이미지 업로드 API
     *
     * @param List<MultipartFile> files
     * @return ApiResponse<UploadFilesResponse>
     */
    @PostMapping("/upload")
    public ApiResponse<UploadFilesResponse> uploadImages(@RequestParam("file") List<MultipartFile> files) {
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


    /**
     * 이미지 삭제 API
     *
     * @param DeleteImagesRequest request
     * @return ApiResponse
     */
    @PostMapping("/delete")
    public ApiResponse deleteImages(@RequestBody DeleteImagesRequest request) {
        // 삭제할 이미지 URL 리스트 추출
        List<String> fileUrls = request.getFileUrls();

        try {
            // S3에서 여러 장의 이미지 삭제
            imageUploadService.deleteFiles(fileUrls);

            return new ApiResponse<>(ResponseMessage.S3_DELETE_SUCCESS.getCode(), ResponseMessage.S3_DELETE_SUCCESS.getMessage());
        } catch (URISyntaxException e) {
            return new ApiResponse<>(ResponseMessage.S3_DELETE_FAIL.getCode(), e.getMessage());
        } catch (NotFoundFileInS3Exception e) {
            return new ApiResponse<>(ResponseMessage.S3_DELETE_FAIL.getCode(), e.getMessage());
        }
    }
}
