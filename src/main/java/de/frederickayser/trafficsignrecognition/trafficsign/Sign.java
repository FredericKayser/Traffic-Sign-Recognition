package de.frederickayser.trafficsignrecognition.trafficsign;

import lombok.Getter;

/**
 * by Frederic on 04.05.20(16:18)
 */
public class Sign {

    @Getter
    private final Type type;
    private int seen;

    public Sign(Type type) {
        this.type = type;
        this.seen = 0;
    }

    public void seen() {
        seen++;
    }

    public void notSeen() {
        seen = 0;
    }

    public boolean isConfirmed() {
        return seen >= 3;
    }

}
