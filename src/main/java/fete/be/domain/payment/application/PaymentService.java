package fete.be.domain.payment.application;

import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.admin.application.dto.response.MemberDto;
import fete.be.domain.admin.application.dto.response.PaymentDto;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PosterService posterService;


    /**
     * 해당 이벤트의 모든 결제 기록 조회
     */
    public List<PaymentDto> getPayments(Long posterId, int page, int size) {
        // 포스터 및 이벤트 조회
        Poster poster = posterService.findPosterByPosterId(posterId);
        Event event = poster.getEvent();

        // 페이징을 위한 offset 계산
        int offset = page * size;

        List<PaymentDto> result = event.getPayments().stream()
                .skip(offset)
                .limit(size)
                .map(payment -> {
                    Member member = payment.getMember();
                    MemberDto memberDto = new MemberDto(
                            member.getMemberId(),
                            member.getEmail(),
                            member.getProfileImage().getImageUrl(),
                            member.getUserName(),
                            member.getIntroduction(),
                            member.getBirth(),
                            member.getGender(),
                            member.getPhoneNumber(),
                            member.getRole(),
                            member.getCreatedAt(),
                            member.getStatus()
                    );

                    return new PaymentDto(
                            memberDto,
                            payment.getTicketType(),
                            payment.getTicketPrice(),
                            payment.getIsPaid(),
                            payment.getTotalAmount(),
                            payment.getPaymentAt(),
                            payment.getParticipant().getIsParticipated()
                    );
                })
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 해당 이벤트의 총 수익 조회
     */
    public int getTotalProfit(Long posterId) {
        // 포스터 및 이벤트 조회
        Poster poster = posterService.findPosterByPosterId(posterId);
        Event event = poster.getEvent();

        return event.getTotalProfit();
    }

    /**
     * 계좌 정보 조회
     */
    public AccountDto getAccount(Long posterId) {
        // 포스터 및 이벤트 조회
        Poster poster = posterService.findPosterByPosterId(posterId);
        Event event = poster.getEvent();

        AccountDto account = new AccountDto(event);
        return account;
    }
}
