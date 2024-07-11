package fete.be.domain.ticket.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.ticket.application.dto.response.TicketDto;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final MemberService memberService;
    private final ParticipantRepository participantRepository;

    public List<TicketDto> getTickets() {
        Member member = memberService.findMemberByEmail();

        return participantRepository.findByMemberAndPaymentIsPaidTrue(member)
                .stream()
                .map(participant -> new TicketDto(
                        participant.getParticipantId(),
                        participant.getEvent().getEventType(),
                        participant.getEvent().getStartDate(),
                        participant.getEvent().getEndDate(),
                        participant.getEvent().getAddress(),
                        participant.getEvent().getTicketName(),
                        participant.getEvent().getTicketPrice()
                ))
                .collect(Collectors.toList());
    }
}
