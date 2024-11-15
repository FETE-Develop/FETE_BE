package fete.be.domain.poster.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SearchPostersRequest {
    private String keyword;  // 검색어
}
