package fete.be.domain.banner.persistence;

import fete.be.domain.poster.persistence.Poster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    boolean existsByPoster(Poster poster);
}
