package dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AddLinkDTO(@NotEmpty String link, List<String> tags, List<String> filters) {
    @Override
    public String toString() {
        StringBuilder defaultMessage = new StringBuilder(String.format("Ссылка: %s%n", link));
        if (!tags.isEmpty()) {
            defaultMessage.append("Теги: ");
            for (int i = 0; i < tags.size(); i++) {
                if (i != tags.size() - 1) {
                    defaultMessage.append(tags.get(i)).append(", ");
                } else {
                    defaultMessage.append(tags.get(i)).append("\n");
                }
            }
        }

        if (!filters.isEmpty()) {
            defaultMessage.append("Фильтры: ");
            for (int i = 0; i < filters.size(); i++) {
                if (i != filters.size() - 1) {
                    defaultMessage.append(filters.get(i)).append(", ");
                } else {
                    defaultMessage.append(filters.get(i));
                }
            }
        }

        return defaultMessage.toString();
    }
}
