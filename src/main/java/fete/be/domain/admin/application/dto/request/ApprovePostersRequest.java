package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class ApprovePostersRequest {
    List<Long> posterIds;
}
