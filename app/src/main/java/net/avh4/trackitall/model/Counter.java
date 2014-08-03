package net.avh4.trackitall.model;

public class Counter {
    private final String type;
    private final int buttonId;
    private final int labelId;

    public Counter(final String type, int labelId, int buttonId) {
        this.type = type;
        this.labelId = labelId;
        this.buttonId = buttonId;
    }

    public String getType() {
        return type;
    }

    public int getButtonId() {
        return buttonId;
    }

    public int getLabelId() {
        return labelId;
    }
}
