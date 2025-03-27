package backend.academy.scrapper.entities.JPA;

import backend.academy.scrapper.utils.LinkType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "url")
@Getter
@Setter
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Enumerated(EnumType.STRING)
    private LinkType linkType;

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Content> contents = new ArrayList<>();

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Link> links = new ArrayList<>();

    public void addLink(Link link) {
        links.add(link);
        link.url(this);
    }

    public void removeLink(Link link) {
        links.remove(link);
        link.url(null);
    }

    public void addContent(Content content) {
        contents.add(content);
        content.url(this);
    }

    public void removeContent() {
        contents.forEach(content -> content.setUrl(null));
        contents.clear();
    }
}
