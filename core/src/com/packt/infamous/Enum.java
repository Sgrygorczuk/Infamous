package com.packt.infamous;

public enum Enum {
    BOLT (0),
    BOMB (1),
    TORPEDO (2),
    EXPLOSION (3);

    Enum(int i) {
    }

    public static Enum fromInteger(int x) {
        switch(x) {
            case 0:
                return BOLT;
            case 1:
                return BOMB;
            case 2:
                return TORPEDO;
            case 3:
                return EXPLOSION;
        }
        return null;
    }
}