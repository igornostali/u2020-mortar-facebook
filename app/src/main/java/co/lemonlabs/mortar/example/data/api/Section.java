package co.lemonlabs.mortar.example.data.api;

public enum Section {

    HOT("hot"),
    TOP("top"),
    USER("user");

    public final String value;

    @Override
    public String toString() {
        return value;
    }

    Section(String value) {
        this.value = value;
    }
}
