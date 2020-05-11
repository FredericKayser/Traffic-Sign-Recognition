package de.frederickayser.trafficsignrecognition.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * by Frederic on 11.05.20(16:53)
 */
@AllArgsConstructor
@Getter
public class Tuple<A, B> {

    private final A a;
    private final B b;

}
