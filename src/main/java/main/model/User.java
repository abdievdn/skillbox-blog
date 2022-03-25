package main.model;

import lombok.*;

import javax.persistence.*;
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
    private boolean isModerator;

    @Column(nullable = false, name = "moderation_count")
    private int moderationCount;

    @Column(nullable = false, name = "reg_time")
    private Date regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    private String photo;

    public User(boolean isModerator, int moderationCount, Date regTime, String name, String email, String password) {
        this.isModerator = isModerator;
        this.moderationCount = moderationCount;
        this.regTime = regTime;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
