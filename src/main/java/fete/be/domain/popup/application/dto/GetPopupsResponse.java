package fete.be.domain.popup.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class GetPopupsResponse {
    private List<PopupDto> popups;
}
