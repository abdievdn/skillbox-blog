package main.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private short value; // -1, 0, 1
}
