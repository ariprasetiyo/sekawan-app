package com.arprast.sekawan.type;

public enum RegistrationActivityType {
    NOTHING(0),
    OPEN_CAM_FOR_PHOTO(1),
    OPEN_HOME(2);

    private int id;

    RegistrationActivityType(int id) {
        this.id = id;
    }
}
