package main.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, columnDefinition = "TINYINT", length = 1, name = "is_active")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('NEW','ACCEPTED','DECLINED')", name = "moderation_status")
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false, name = "view_count")
    private int viewCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post2tag")
    private Set<Tag> tags;
}
