package backend.academy.scrapper.entities.JPA;

import dto.Settings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
public class UserSettings {
    @Id
    private Long userId;

    @Column(name = "notify_mood", nullable = false)
    @Enumerated(EnumType.STRING)
    private Settings notifyMood;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notify_time")
    private LocalTime notifyTime;
}
