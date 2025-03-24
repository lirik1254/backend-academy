package backend.academy.scrapper.entities.JPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "users")
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Link> links = new ArrayList<>();

    public void addLink(Link link) {
        links.add(link);
        link.users(this);
    }

    public void delete() {
        List<Link> linksToDelete = new ArrayList<>(links);
        links.clear();
        linksToDelete.forEach(link -> {
            link.users(null);
            link.deleteLink();
        });
    }
}
