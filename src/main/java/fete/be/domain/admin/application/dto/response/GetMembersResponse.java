package fete.be.domain.admin.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMembersResponse {
    List<MemberDto> users;
}
