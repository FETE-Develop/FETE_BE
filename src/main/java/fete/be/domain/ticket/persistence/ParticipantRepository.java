package fete.be.domain.ticket.persistence;

import fete.be.domain.member.persistence.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByMemberAndPaymentIsPaidTrue(Member member);
}
