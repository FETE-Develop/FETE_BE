package fete.be.domain.admin.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetSimplePostersResponse {
    private List<SimplePosterDto> simplePosters;
}
