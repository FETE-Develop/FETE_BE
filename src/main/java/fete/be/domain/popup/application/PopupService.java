package fete.be.domain.popup.application;

import fete.be.domain.admin.application.dto.request.CreatePopupRequest;
import fete.be.domain.admin.application.dto.request.ModifyPopupRequest;
import fete.be.domain.popup.application.dto.PopupDto;
import fete.be.domain.popup.persistence.Popup;
import fete.be.domain.popup.persistence.PopupRepository;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PopupService {

    private final PosterService posterService;
    private final PopupRepository popupRepository;


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
                () -> new IllegalArgumentException("해당 팝업이 존재하지 않습니다.")
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
                () -> new IllegalArgumentException("해당 팝업이 존재하지 않습니다.")
        );

        // 삭제 실행
        popupRepository.delete(popup);
    }

    public List<PopupDto> getPopups() {
        return popupRepository.findAll().stream()
                .map(popup -> new PopupDto(
                        popup.getPopupId(),
                        popup.getImageUrl(),
                        popup.getPosterId()
                ))
                .collect(Collectors.toList());
    }
}
