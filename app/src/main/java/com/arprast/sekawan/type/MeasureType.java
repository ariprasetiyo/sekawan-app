package com.arprast.sekawan.type;

public enum MeasureType {

    M("M"),
    M2("M2"),
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
            case "M2":
                return M2;
            case "KG":
                return KG;
            default:
                return null;
        }
    }
}
