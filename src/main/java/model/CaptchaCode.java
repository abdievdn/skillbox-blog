package model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String code; // picture link

    @Column(nullable = false, columnDefinition = "TINYTEXT", name = "secret_code")
    private String secretCode; // parameter
}
