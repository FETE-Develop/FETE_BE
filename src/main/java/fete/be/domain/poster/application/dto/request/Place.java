package fete.be.domain.poster.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Place {
    private String address;  // 상세 주소
    private String simpleAddress;  // 간단 주소
    private double latitude;  // 위도
    private double longitude;  // 경도
}
