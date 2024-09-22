package fete.be.domain.popup.persistence;

import fete.be.domain.member.persistence.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopupDismissRepository extends JpaRepository<PopupDismiss, Long> {
    @Query("select pd.popup.popupId from PopupDismiss pd where pd.member = :member")
    List<Long> findDismissPopupIdsByMember(@Param("member") Member member);
}
