package fete.be.domain.poster.application.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ApprovePostersRequest {
    List<Long> posterIds;
}
