package io.getmedusa.medusa.core.util;

import java.util.Objects;

public class Pair {
    final int a;
    final int b;

    Pair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return a == pair.a && b == pair.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "{" + a + ", " + b + '}';
    }
}
