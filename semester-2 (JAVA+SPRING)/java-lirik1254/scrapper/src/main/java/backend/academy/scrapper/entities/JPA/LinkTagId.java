package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class LinkTagId implements Serializable {
    @Embedded
    private LinkId linkId;

    @Column(name = "tag", nullable = false)
    private String tag;

    public LinkTagId() {}

    public LinkTagId(LinkId linkId, String tag) {
        this.linkId = linkId;
        this.tag = tag;
    }
}
