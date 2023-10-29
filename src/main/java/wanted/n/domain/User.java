package wanted.n.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import wanted.n.dto.UserSignUpRequest;
import wanted.n.enums.UserRole;
import wanted.n.enums.UserStatus;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String account;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    public static User from(UserSignUpRequest userSignUpRequest) {
        return User.builder()
                .account(userSignUpRequest.getAccount())
                .email(userSignUpRequest.getEmail())
                .password(userSignUpRequest.getPassword())
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.UNVERIFIED)
                .build();
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
