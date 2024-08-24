package fete.be.domain.member.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedMemberRepository extends JpaRepository<BlockedMember, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}
