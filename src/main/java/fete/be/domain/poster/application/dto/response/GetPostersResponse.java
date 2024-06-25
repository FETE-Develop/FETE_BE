package fete.be.domain.poster.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetPostersResponse {
    private List<PosterDto> posters;
}
