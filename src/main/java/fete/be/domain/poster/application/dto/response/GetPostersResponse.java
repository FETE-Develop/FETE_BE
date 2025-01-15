package fete.be.domain.poster.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetPostersResponse {
    private List<PosterDto> posters;
    private int totalPages;
    private long totalElements;

    public GetPostersResponse(Page<PosterDto> pageInfo) {
        this.posters = pageInfo.getContent();
        this.totalElements = pageInfo.getTotalElements();
        this.totalPages = (int) Math.ceil((double) totalElements / pageInfo.getSize());
    }
}
