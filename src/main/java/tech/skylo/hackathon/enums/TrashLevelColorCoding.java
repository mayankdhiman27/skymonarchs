package tech.skylo.hackathon.enums;

public enum TrashLevelColorCoding {

    FULL("#FF0000"),
    HAL_FULL("#FFFC33"),
    ALMOST_EMPTY("#33FF52");

    String value;

    TrashLevelColorCoding(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
