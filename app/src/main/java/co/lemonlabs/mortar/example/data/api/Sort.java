package co.lemonlabs.mortar.example.data.api;

public enum Sort {

    VIRAL("viral"),
    TIME("time");

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    Sort(String value) {
        this.value = value;
    }
}
