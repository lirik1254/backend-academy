package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "link_tags", schema = "scrapper")
@Getter
@Setter
public class LinkTag {
    @EmbeddedId
    private LinkTagId id;

    /** Здесь всего **один** @MapsId, он ссылается на свойство linkId внутри LinkTagId */
    @MapsId("linkId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
        @JoinColumn(name = "url_id", referencedColumnName = "url_id")
    })
    private Link link;

    public LinkTag() {}

    public LinkTag(Link link, String tag) {
        this.link = link;
        this.id = new LinkTagId(link.id(), tag);
    }

    public String getTag() {
        return id.tag();
    }
}
