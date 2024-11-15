package fete.be.domain.notice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetNoticesResponse {
    private List<SimpleNotice> simpleNotices;
}
