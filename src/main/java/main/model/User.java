package main.model;

import lombok.*;
import main.security.Role;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1, name = "is_moderator")
    private int isModerator;

    @Column(nullable = false, name = "moderation_count")
    private int moderationCount;

    @Column(nullable = false, name = "reg_time")
    private LocalDateTime regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    private String photo;

    @Transient
    private Role role;

    public Role getRole() {
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }

}
