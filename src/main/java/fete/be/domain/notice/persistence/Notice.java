package fete.be.domain.notice.persistence;

import fete.be.domain.admin.application.dto.request.CreateNoticeRequest;
import fete.be.domain.admin.application.dto.request.ModifyNoticeRequest;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "title")
    private String title;  // 제목
    @Column(name = "content")
    private String content;  // 내용

    private LocalDateTime createdAt;  // 생성 일자


    // 생성 메서드
    public static Notice createNotice(CreateNoticeRequest request) {
        Notice notice = new Notice();

        notice.title = request.getTitle();
        notice.content = request.getContent();
        notice.createdAt = LocalDateTime.now();

        return notice;
    }

    // 수정 메서드
    public static Notice modifyNotice(Notice notice, ModifyNoticeRequest request) {
        notice.title = request.getTitle();
        notice.content = request.getContent();
        notice.createdAt = LocalDateTime.now();

        return notice;
    }


}
