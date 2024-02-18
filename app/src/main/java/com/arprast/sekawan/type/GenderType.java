package com.arprast.sekawan.type;

public enum GenderType {

    MALE("Male"),
    FEMALE("Female");

    public String stringValue;

    GenderType(String stringValue) {
        this.stringValue = stringValue;
    }

    public static GenderType valueOfString(String stringValue) {

        if (stringValue == null) {
            return null;
        }

        switch (stringValue.toLowerCase()) {
            case "Male":
                return MALE;
            case "Female":
                return FEMALE;
            default:
                return null;
        }
    }
}
