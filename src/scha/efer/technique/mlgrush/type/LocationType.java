package scha.efer.technique.mlgrush.type;

import scha.efer.technique.util.external.CC;

public enum LocationType {

    BED_TOP_1(CC.GRAY + "Bed Top 1"),
    BED_BOTTOM_1(CC.GRAY + "Bed Bottom 1"),
    BED_TOP_2(CC.GRAY + "Bed Top 2"),
    BED_BOTTOM_2(CC.GRAY + "Bed Bottom 2");

    private String description;

    LocationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
