package wanted.n.dto;

import lombok.Builder;
import lombok.Getter;
import wanted.n.domain.User;
import wanted.n.enums.UserRole;

@Getter
@Builder
public class TokenIssuanceDTO {
    private Long id;
    private String email;
    private String account;
    private UserRole userRole;

    public static TokenIssuanceDTO from(User user) {
        return TokenIssuanceDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .account(user.getAccount())
                .userRole(user.getUserRole())
                .build();
    }
}