package fete.be.domain.member.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Page<Member> findAll(Pageable pageable);

    @Query("SELECT m.fcmToken FROM Member m WHERE m.fcmToken IS NOT NULL")
    List<String> findAllFcmTokens();
}
