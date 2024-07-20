package fete.be.domain.poster.persistence;

import fete.be.domain.member.persistence.Member;
import fete.be.global.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PosterRepository extends JpaRepository<Poster, Long> {
    Page<Poster> findByStatus(Status status, Pageable pageable);

    Page<Poster> findByMember(Member member, Pageable pageable);

    Page<Poster> findByPosterIdIn(List<Long> posterIds, Pageable pageable);

    Optional<Poster> findByStatusAndPosterId(Status status, Long posterId);
}
