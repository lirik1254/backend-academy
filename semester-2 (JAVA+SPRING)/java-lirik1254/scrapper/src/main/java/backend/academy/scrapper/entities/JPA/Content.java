package backend.academy.scrapper.entities.JPA;

import backend.academy.scrapper.utils.LinkType;
import dto.ContentDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answer;
    private String creationTime;
    private String title;

    private String userName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Url url;

    public void setUrl(Url url) {
        this.url = url;
        if (url != null && !url.contents().contains(this)) {
            url.addContent(this);
        }
    }

    public static Content createFromDTO(LinkType linkType, ContentDTO dto, Url url) {
        Content content =
                switch (linkType) {
                    case GITHUB -> {
                        GithubContent gc = new GithubContent();
                        gc.updatedType(dto.type()); // Устанавливаем специфичное поле
                        yield gc;
                    }
                    case STACKOVERFLOW -> {
                        StackOverflowContent soc = new StackOverflowContent();
                        soc.updatedType(dto.type()); // Устанавливаем специфичное поле
                        yield soc;
                    }
                };

        content.answer(dto.answer());
        content.creationTime(dto.creationTime());
        content.title(dto.title());
        content.userName(dto.userName());
        content.setUrl(url);

        return content;
    }
}
