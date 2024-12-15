package fete.be.domain.poster.persistence;

import fete.be.domain.member.persistence.Member;
import fete.be.global.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PosterRepository extends JpaRepository<Poster, Long> {
    Page<Poster> findByStatus(Status status, Pageable pageable);

    Page<Poster> findByMemberAndStatusNot(Member member, Status status, Pageable pageable);

    Page<Poster> findByPosterIdIn(List<Long> posterIds, Pageable pageable);

    @Query("SELECT p FROM Poster p WHERE (p.event.eventName LIKE %:keyword% OR p.event.description LIKE %:keyword%) AND p.status = 'ACTIVE'")
    Page<Poster> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Poster> findByStatusAndPosterId(Status status, Long posterId);

    @Query("select p from Poster p where p.status = 'ACTIVE' and p.event.endDate < :now")
    List<Poster> findEndDateBeforeNow(@Param("now")LocalDateTime now);  // 종료된 이벤트 조회

    @Query("select p from Poster p where p.status = 'END' and p.event.endDate >= :sevenDaysAgo and p.event.endDate < :tomorrow")
    List<Poster> findEndedWithin7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, @Param("tomorrow") LocalDateTime tomorrow);
}
