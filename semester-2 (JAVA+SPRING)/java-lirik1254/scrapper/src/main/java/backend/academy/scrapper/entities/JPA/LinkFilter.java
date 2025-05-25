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
@Table(name = "link_filters", schema = "scrapper")
@Getter
@Setter
public class LinkFilter {
    @EmbeddedId
    private LinkFilterId id;

    @MapsId("linkId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
        @JoinColumn(name = "url_id", referencedColumnName = "url_id")
    })
    private Link link;

    public LinkFilter() {}

    public LinkFilter(Link link, String filter) {
        this.link = link;
        this.id = new LinkFilterId(link.id(), filter);
    }

    public String getFilter() {
        return id.filter();
    }
}
