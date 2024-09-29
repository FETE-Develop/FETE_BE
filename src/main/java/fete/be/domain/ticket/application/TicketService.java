package fete.be.domain.ticket.application;

import fete.be.domain.event.application.QRCodeService;
import fete.be.domain.event.exception.NotFoundEventException;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.event.persistence.EventRepository;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.payment.persistence.PaymentRepository;
import fete.be.domain.ticket.application.dto.response.*;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final MemberService memberService;
    private final QRCodeService qrCodeService;
    private final EventRepository eventRepository;
    private final PaymentRepository paymentRepository;
    private final ParticipantRepository participantRepository;

    public List<TicketEventDto> getEvents() {
        Member member = memberService.findMemberByEmail();

        return participantRepository.findByMember(member).stream()
                .collect(Collectors.groupingBy(participant -> participant.getPayment().getPaymentCode()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Participant> participants = entry.getValue();

                    Event event = participants.get(0).getEvent();
                    Payment payment = participants.get(0).getPayment();

                    return new TicketEventDto(event, payment, entry.getKey());
                })
                .collect(Collectors.toList());
    }

    public SimpleEventDto getEventInfo(Long eventId) {
        // 이벤트 조회
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundEventException(ResponseMessage.EVENT_NO_EXIST.getMessage())
        );

        return new SimpleEventDto(event);
    }

    public List<SimpleTicketDto> getTickets(String paymentCode) {
        return paymentRepository.findByPaymentCode(paymentCode).stream()
                .map(payment -> new SimpleTicketDto(payment))
                .collect(Collectors.toList());
    }

    public GetTicketInfoResponse getTicketInfo(Long participantId) throws Exception {
        // participantId로 Participant 객체 찾아오기
        Participant participant = participantRepository.findById(participantId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.TICKET_NO_EXIST.getMessage())
        );

        // 결제 상태를 검사하기 위해 Payment 객체 조회
        Payment payment = participant.getPayment();
        String qrCode;

        // 결제 상태에 따라 QR 코드 발급
        if (payment.getIsPaid()) {
            // 찾아온 객체를 QR서비스 단의 generateQRCodeBase64에 넣어서 qrCode 발급해오기
            qrCode = qrCodeService.generateQRCodeBase64(participant, 250, 250);
        } else {
            qrCode = "";
        }

        // 리턴 객체 제작
        TicketDto ticket = new TicketDto(participant);

        GetTicketInfoResponse result = new GetTicketInfoResponse(ticket, qrCode);
        return result;
    }

    public String getCustomerKey() {
        // 유저 조회
        Member member = memberService.findMemberByEmail();
        return member.getCustomerKey();
    }
}
