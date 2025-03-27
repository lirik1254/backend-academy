package dto;

public enum UpdateType {
    COMMENT("COMMENT"),
    ANSWER("ANSWER"),
    PR("PR"),
    ISSUE("ISSUE");

    private final String value;

    UpdateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
