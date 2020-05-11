package de.frederickayser.trafficsignrecognition.trafficsign;

import lombok.Getter;

/**
 * by Frederic on 04.05.20(16:18)
 */
public class Sign {

    @Getter
    private final Type type;
    private int seen, notSeen = 0;
    @Getter
    private boolean changed = false;

    public Sign(Type type) {
        this.type = type;
        this.seen = 0;
    }


    public void seen()
    {
        seen++;
        changed = true;
    }

    public void reset() {
        changed = false;
    }

    public void notSeen() {
        notSeen++;
        if(notSeen > 5) {
            seen = 0;
        }
    }

    public boolean isConfirmed() {
        return (seen >= 5);
    }


}
