package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "link")
@Setter
@Getter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "link_tags", joinColumns = @JoinColumn(name = "link_id"))
    @Column(name = "tags")
    private List<String> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "link_filters", joinColumns = @JoinColumn(name = "link_id"))
    @Column(name = "filters")
    private List<String> filters;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "url_id")
    private Url url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private Users users;

    //    public void addContent(Content content) {
    //        this.content.add(content);
    //        content.link(this);
    //    }
    public void deleteLink() {
        if (url != null) {
            url.links().remove(this);
            url = null;
        }

        // Удаляем связь с пользователем
        if (users != null) {
            users.links().remove(this);
            users = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return linkId != null && linkId.equals(link.linkId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
