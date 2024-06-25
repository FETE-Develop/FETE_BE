package fete.be.domain.poster.persistence;

import fete.be.global.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosterRepository extends JpaRepository<Poster, Long> {
    Page<Poster> findByStatus(Status status, Pageable pageable);
}
