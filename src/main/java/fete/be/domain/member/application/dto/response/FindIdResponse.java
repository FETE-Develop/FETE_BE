package fete.be.domain.member.application.dto.response;

import fete.be.domain.member.persistence.MemberType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindIdResponse {
    private MemberType memberType;
    @Nullable
    private String email;

    public FindIdResponse(MemberType memberType, String email) {
        this.memberType = memberType;
        this.email = email;
    }

    public FindIdResponse(MemberType memberType) {
        this.memberType = memberType;
    }
}
