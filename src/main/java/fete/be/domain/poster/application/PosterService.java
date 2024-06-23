package fete.be.domain.poster.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.poster.persistence.PosterRepository;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PosterService {

    private final PosterRepository posterRepository;
    private final MemberService memberService;

    @Transactional
    public Long writePoster(Member member, WritePosterRequest request) {
        Poster poster = Poster.createPoster(member, request);
        Poster savedPoster = posterRepository.save(poster);
        return savedPoster.getPosterId();
    }

    public Poster findPosterByPosterId(Long posterId) {
        return posterRepository.findById(posterId).orElseThrow(
                () -> new IllegalArgumentException("해당 포스터가 존재하지 않습니다."));
    }

    @Transactional
    public Long updatePoster(Long posterId, ModifyPosterRequest request) {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.findMemberByEmail(email);

        Poster poster = posterRepository.findById(posterId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage()));

        // 수정을 요청한 사용자가 해당 포스터의 작성자가 아닌 경우
        if (!poster.getMember().equals(member)) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
        }

        // 업데이트 진행
        Poster updatePoster = Poster.updatePoster(poster, request);
        return updatePoster.getPosterId();
    }

}
