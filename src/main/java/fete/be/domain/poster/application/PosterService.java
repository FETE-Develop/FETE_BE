package fete.be.domain.poster.application;

import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.poster.persistence.PosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PosterService {

    private final PosterRepository posterRepository;

    @Transactional
    public Long writePoster(Member member, WritePosterRequest request) {
        Poster poster = Poster.createPoster(member, request);
        Poster savedPoster = posterRepository.save(poster);
        return savedPoster.getPosterId();
    }

}
