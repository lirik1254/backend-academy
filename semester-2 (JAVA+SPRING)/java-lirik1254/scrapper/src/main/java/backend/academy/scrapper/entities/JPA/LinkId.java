package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class LinkId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "url_id")
    private Long urlId;

    public static LinkId of(Long userId, Long urlId) {
        LinkId linkId = new LinkId();
        linkId.userId(userId);
        linkId.urlId(urlId);
        return linkId;
    }
}
