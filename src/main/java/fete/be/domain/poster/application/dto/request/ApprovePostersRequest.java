package fete.be.domain.poster.application.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ApprovePostersRequest {
    List<Long> posterIds;
}
