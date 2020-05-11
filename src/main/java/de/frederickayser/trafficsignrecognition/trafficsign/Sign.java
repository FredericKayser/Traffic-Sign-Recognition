package de.frederickayser.trafficsignrecognition.trafficsign;

import lombok.Getter;

/**
 * by Frederic on 04.05.20(16:18)
 */
public class Sign {

    @Getter
    private final Type type;
    private int seen;
    private double probability = 0;

    public Sign(Type type) {
        this.type = type;
        this.seen = 0;
    }

    public void seen(double probability) {
        seen++;
        this.probability += probability;
    }

    public void reset()
    {
        seen = 0;
        probability = 0;
    }

    public double getProbability() {
        return (probability/(double)seen);
    }

}
