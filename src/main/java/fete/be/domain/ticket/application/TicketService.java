package fete.be.domain.ticket.application;

import fete.be.domain.event.application.QRCodeService;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.ticket.application.dto.response.GetTicketInfoResponse;
import fete.be.domain.ticket.application.dto.response.TicketDto;
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
    private final ParticipantRepository participantRepository;

    public List<TicketDto> getTickets() {
        Member member = memberService.findMemberByEmail();

        return participantRepository.findByMemberAndPaymentIsPaidTrue(member)
                .stream()
                .map(participant -> new TicketDto(
                        participant.getParticipantId(),
                        participant.getEvent().getEventName(),
                        participant.getEvent().getStartDate(),
                        participant.getEvent().getEndDate(),
                        participant.getEvent().getAddress(),
                        participant.getPayment().getTicketType(),
                        participant.getPayment().getTicketPrice()
                ))
                .collect(Collectors.toList());
    }

    public GetTicketInfoResponse getTicketInfo(Long participantId) throws Exception {
        // participantId로 Participant 객체 찾아오기
        Participant participant = participantRepository.findById(participantId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.TICKET_NO_EXIST.getMessage())
        );

        // 찾아온 객체를 QR서비스 단의 generateQRCodeBase64에 넣어서 qrCode 발급해오기
        String qrCode = qrCodeService.generateQRCodeBase64(participant, 250, 250);

        // 리턴 객체 제작
        TicketDto ticket = new TicketDto(
                participant.getParticipantId(),
                participant.getEvent().getEventName(), participant.getEvent().getStartDate(),
                participant.getEvent().getEndDate(), participant.getEvent().getAddress(),
                participant.getPayment().getTicketType(), participant.getPayment().getTicketPrice());
        GetTicketInfoResponse result = new GetTicketInfoResponse(ticket, qrCode);

        return result;
    }
}
