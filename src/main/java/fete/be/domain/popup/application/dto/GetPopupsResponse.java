package fete.be.domain.popup.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetPopupsResponse {
    private List<PopupDto> popups;
}
