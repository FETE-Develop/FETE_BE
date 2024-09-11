package fete.be.domain.notice.application;

import fete.be.domain.admin.application.dto.request.CreateNoticeRequest;
import fete.be.domain.admin.application.dto.request.ModifyNoticeRequest;
import fete.be.domain.admin.exception.NotFoundNoticeException;
import fete.be.domain.notice.persistence.Notice;
import fete.be.domain.notice.persistence.NoticeRepository;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;


    @Transactional
    public Long createNotice(CreateNoticeRequest request) {
        Notice notice = Notice.createNotice(request);
        Notice savedNotice = noticeRepository.save(notice);

        return savedNotice.getNoticeId();
    }

    @Transactional
    public Long modifyNotice(Long noticeId, ModifyNoticeRequest request) {
        // Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NotFoundNoticeException(ResponseMessage.NOTICE_NO_EXIST.getMessage())
        );

        // 수정 실행
        Notice modifiedNotice = Notice.modifyNotice(notice, request);
        Notice savedNotice = noticeRepository.save(modifiedNotice);

        return savedNotice.getNoticeId();
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        // Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NotFoundNoticeException(ResponseMessage.NOTICE_NO_EXIST.getMessage())
        );

        // 삭제 실행
        noticeRepository.delete(notice);
    }


}
