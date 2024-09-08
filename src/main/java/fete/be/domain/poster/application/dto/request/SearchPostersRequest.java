package fete.be.domain.poster.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchPostersRequest {
    private String keyword;  // 검색어
}
