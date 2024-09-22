package fete.be.domain.popup.persistence;

import fete.be.domain.member.persistence.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class PopupDismiss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_dismiss_id")
    private Long popupDismissId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    @Column(name = "dismiss_at")
    private LocalDateTime dismissAt;  // 팝업 그만보기 설정한 시간


    // 생성 메서드
    public static PopupDismiss createPopupDismiss(Member member, Popup popup) {
        PopupDismiss popupDismiss = new PopupDismiss();

        popupDismiss.member = member;
        popupDismiss.popup = popup;
        popupDismiss.dismissAt = LocalDateTime.now();

        return popupDismiss;
    }
}
