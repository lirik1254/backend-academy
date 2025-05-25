package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
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

    //    @ElementCollection(fetch = FetchType.EAGER)
    //    @CollectionTable(
    //            name = "link_tags",
    //            schema = "scrapper",
    //            joinColumns = {
    //                @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
    //                @JoinColumn(name = "url_id", referencedColumnName = "url_id")
    //            })
    //    @Column(name = "tag")
    //    private List<String> tags = new ArrayList<>();
    //
    //    @ElementCollection(fetch = FetchType.EAGER)
    //    @CollectionTable(
    //            name = "link_filters",
    //            schema = "scrapper",
    //            joinColumns = {
    //                @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
    //                @JoinColumn(name = "url_id", referencedColumnName = "url_id")
    //            })
    //    @Column(name = "filter")
    //    private List<String> filters = new ArrayList<>();

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LinkTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LinkFilter> filters = new ArrayList<>();

    public void clearFilters() {
        filters.forEach(filter -> filter.link(null));
        filters.clear();
    }

    public void clearTags() {
        tags.forEach(tag -> tag.link(null));
        tags.clear();
    }

    public void addTag(String tag) {
        tags.add(new LinkTag(this, tag));
    }

    public void addFilter(String filter) {
        filters.add(new LinkFilter(this, filter));
    }

    public void addTag(List<String> tags) {
        tags.forEach(this::addTag);
    }

    public void addFilter(List<String> filters) {
        filters.forEach(this::addFilter);
    }

    public List<String> getTags() {
        return tags.stream().map(LinkTag::getTag).toList();
    }

    public List<String> getFilters() {
        return filters.stream().map(LinkFilter::getFilter).toList();
    }

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
