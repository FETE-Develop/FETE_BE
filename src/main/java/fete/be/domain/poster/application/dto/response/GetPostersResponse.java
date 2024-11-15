package fete.be.domain.poster.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetPostersResponse {
    private List<PosterDto> posters;
    private int totalPages;
}
