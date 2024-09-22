package fete.be.domain.popup.application;

import fete.be.domain.admin.application.dto.request.CreatePopupRequest;
import fete.be.domain.admin.application.dto.request.ModifyPopupRequest;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.popup.application.dto.PopupDto;
import fete.be.domain.popup.exception.NotFoundPopupException;
import fete.be.domain.popup.persistence.Popup;
import fete.be.domain.popup.persistence.PopupDismiss;
import fete.be.domain.popup.persistence.PopupDismissRepository;
import fete.be.domain.popup.persistence.PopupRepository;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PopupService {

    private final MemberService memberService;
    private final PosterService posterService;
    private final PopupRepository popupRepository;
    private final PopupDismissRepository popupDismissRepository;


    @Transactional
    public Long createPopup(CreatePopupRequest request) {
        // 연결할 posterId 값이 있다면, DB 상에 존재하는 포스터인지 검사
        if (request.getPosterId() != null) {
            Poster findPoster = posterService.findPosterByPosterId(request.getPosterId());
        }

        // 팝업 생성 후, DB에 저장
        Popup popup = Popup.createPopup(request);
        Popup savedPopup = popupRepository.save(popup);

        return savedPopup.getPopupId();
    }

    @Transactional
    public Long modifyPopup(Long popupId, ModifyPopupRequest request) {
        // 수정할 팝업 조회
        Popup popup = popupRepository.findById(popupId).orElseThrow(
                () -> new NotFoundPopupException(ResponseMessage.POPUP_NO_EXIST.getMessage())
        );

        // request에 posterId 값이 있다면, DB 상에 존재하는 포스터인지 검사
        if (request.getPosterId() != null) {
            Poster findPoster = posterService.findPosterByPosterId(request.getPosterId());
        }

        Popup modifiedPopup = Popup.modifyPopup(popup, request);
        Popup savedPopup = popupRepository.save(modifiedPopup);

        return savedPopup.getPopupId();
    }

    @Transactional
    public void deletePopup(Long popupId) {
        // 삭제할 팝업 조회
        Popup popup = popupRepository.findById(popupId).orElseThrow(
                () -> new NotFoundPopupException(ResponseMessage.POPUP_NO_EXIST.getMessage())
        );

        // 삭제 실행
        popupRepository.delete(popup);
    }

    public List<PopupDto> getPopups() {
        // 유저 조회
        Member member = memberService.findMemberByEmail();

        // 사용자가 그만보기 처리한 팝업의 아이디 리스트
        List<Long> dismissedPopupIds = popupDismissRepository.findDismissPopupIdsByMember(member);

        // 그만보기 처리된 팝업을 제외한 팝업을 조회하여 반환
        return popupRepository.findAll().stream()
                .filter(popup -> !dismissedPopupIds.contains(popup.getPopupId()))
                .map(popup -> new PopupDto(
                        popup.getPopupId(),
                        popup.getImageUrl(),
                        popup.getPosterId()
                ))
                .collect(Collectors.toList());
    }

    public List<PopupDto> getGuestPopups() {
        return popupRepository.findAll().stream()
                .map(popup -> new PopupDto(
                        popup.getPopupId(),
                        popup.getImageUrl(),
                        popup.getPosterId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void dismissPopup(Long popupId) {
        // 유저 조회
        Member member = memberService.findMemberByEmail();

        // 차단할 팝업 조회
        Popup popup = popupRepository.findById(popupId).orElseThrow(
                () -> new NotFoundPopupException(ResponseMessage.POPUP_NO_EXIST.getMessage())
        );

        // 팝업 차단 DB에 저장
        PopupDismiss popupDismiss = PopupDismiss.createPopupDismiss(member, popup);
        popupDismissRepository.save(popupDismiss);
    }
}
