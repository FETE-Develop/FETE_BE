package fete.be.domain.image.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFilesResponse {
    private List<String> imageUrls;  // 변환된 이미지 링크 리스트
}
