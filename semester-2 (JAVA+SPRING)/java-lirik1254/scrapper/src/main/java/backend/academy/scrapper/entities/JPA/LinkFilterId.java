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
public class LinkFilterId implements Serializable {
    @Embedded
    private LinkId linkId;

    @Column(name = "filter", nullable = false)
    private String filter;

    public LinkFilterId() {}

    public LinkFilterId(LinkId linkId, String filter) {
        this.linkId = linkId;
        this.filter = filter;
    }
}
