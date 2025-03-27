package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "link", schema = "scrapper")
@Getter
@Setter
public class Link {
    @EmbeddedId
    private LinkId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "chat_id")
    private User user;

    @MapsId("urlId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "url_id", referencedColumnName = "id")
    private Url url;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "link_tags",
            schema = "scrapper",
            joinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
                @JoinColumn(name = "url_id", referencedColumnName = "url_id")
            })
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "link_filters",
            schema = "scrapper",
            joinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
                @JoinColumn(name = "url_id", referencedColumnName = "url_id")
            })
    @Column(name = "filter")
    private List<String> filters = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.links().contains(this)) {
            user.addLink(this);
        }
    }

    public void setUrl(Url url) {
        this.url = url;
        if (url != null && !url.links().contains(this)) {
            url.addLink(this);
        }
    }

    public void deleteLink() {
        if (url != null) {
            url.links().remove(this);
            url = null;
        }

        if (user != null) {
            user.links().remove(this);
            user = null;
        }
    }
}
