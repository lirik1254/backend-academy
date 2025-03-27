package backend.academy.scrapper.entities.JPA;

import dto.UpdateType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stackoverflow_content")
@Getter
@Setter
public class StackOverflowContent extends Content {
    @Enumerated(EnumType.STRING)
    private UpdateType updatedType;
}
