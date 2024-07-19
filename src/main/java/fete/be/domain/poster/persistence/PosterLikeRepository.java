package fete.be.domain.poster.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PosterLikeRepository extends JpaRepository<PosterLike, Long> {
    Optional<PosterLike> findByMemberIdAndPosterId(Long memberId, Long posterId);
}
