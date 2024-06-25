package fete.be.domain.poster.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.poster.persistence.PosterRepository;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        // 현재 API 요청을 보낸 Member 찾기
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.findMemberByEmail(email);

        // posterId로 포스터 찾기
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

    @Transactional
    public Long deletePoster(Long posterId) {
        // 현재 API 요청을 보낸 Member 찾기
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberService.findMemberByEmail(email);

        // posterId로 포스터 찾기
        Poster poster = posterRepository.findById(posterId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage()));

        // 삭제를 요청한 사용자가 해당 포스터의 작성자가 아닌 경우
        if (!poster.getMember().equals(member)) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
        }

        // 소프트 삭제 실행 (status 필드를 DELETE로 변경)
        Poster deletePoster = Poster.deletePoster(poster);
        return deletePoster.getPosterId();
    }

    public Page<PosterDto> getPosters(Pageable pageable) {
        // status가 ACTIVE인 Poster만 가져오기
        return posterRepository.findByStatus(Status.ACTIVE, pageable)
                .map(poster -> new PosterDto(
                        poster.getTitle(),
                        poster.getPosterImgUrl(),
                        poster.getInstitution(),
                        poster.getManager(),
                        poster.getManagerContact(),
                        poster.getTicketName(),
                        poster.getTicketPrice(),
                        poster.getEvent().getEventType(),
                        poster.getEvent().getStartDate(),
                        poster.getEvent().getEndDate(),
                        poster.getEvent().getAddress(),
                        poster.getEvent().getDescription(),
                        poster.getEvent().getMood()
                ));
    }
}
