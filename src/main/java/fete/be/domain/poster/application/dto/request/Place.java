package fete.be.domain.poster.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    private String address;  // 주소
    private String simpleAddress;  // 간단 주소
    private String detailAddress;  // 상세 주소
    private double latitude;  // 위도
    private double longitude;  // 경도
}
