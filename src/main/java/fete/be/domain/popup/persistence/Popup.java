package fete.be.domain.popup.persistence;

import fete.be.domain.admin.application.dto.request.CreatePopupRequest;
import fete.be.domain.admin.application.dto.request.ModifyPopupRequest;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Popup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long popupId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "poster_id")
    @Nullable
    private Long posterId;


    // 생성 메서드
    public static Popup createPopup(CreatePopupRequest request) {
        Popup popup = new Popup();

        popup.imageUrl = request.getImageUrl();
        popup.posterId = request.getPosterId();

        return popup;
    }

    // 수정 메서드
    public static Popup modifyPopup(Popup popup, ModifyPopupRequest request) {
        popup.imageUrl = request.getImageUrl();
        popup.posterId = request.getPosterId();

        return popup;
    }
}
