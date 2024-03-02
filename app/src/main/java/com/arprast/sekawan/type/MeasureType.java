package com.arprast.sekawan.type;

public enum MeasureType {

    M("M"),
    KG("KG");

    public String stringValue;

    MeasureType(String stringValue) {
        this.stringValue = stringValue;
    }

    public static MeasureType valueOfString(String stringValue) {

        if (stringValue == null) {
            return null;
        }

        switch (stringValue.toLowerCase()) {
            case "M":
                return M;
            case "KG":
                return KG;
            default:
                return null;
        }
    }
}
