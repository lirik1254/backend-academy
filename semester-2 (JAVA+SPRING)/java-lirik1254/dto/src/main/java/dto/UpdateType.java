package dto;

public enum UpdateType {
    COMMENT("Комментарий"),
    ANSWER("Ответ"),
    PR("Pull Request"),
    ISSUE("Issue");

    private final String value;

    UpdateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
