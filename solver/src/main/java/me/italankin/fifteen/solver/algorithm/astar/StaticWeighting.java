package me.italankin.fifteen.solver.algorithm.astar;

import me.italankin.fifteen.solver.Node;

import java.util.Comparator;

public class StaticWeighting implements Comparator<Node> {

    private final float hw;
    private final float mw;

    /**
     * @param h heuristics relaxation coefficient, the more is faster
     * @param m moves relaxation coefficient, the less is usually faster
     */
    public StaticWeighting(float h, float m) {
        if (h < 0) {
            throw new IllegalArgumentException("h must be >= 0");
        }
        if (m < 0) {
            throw new IllegalArgumentException("m must be >= 0");
        }
        this.hw = h;
        this.mw = m;
    }

    @Override
    public int compare(Node lhs, Node rhs) {
        return Float.compare(
                lhs.heuristicsValue * hw + lhs.moves * mw,
                rhs.heuristicsValue * hw + rhs.moves * mw
        );
    }

    @Override
    public String toString() {
        return "StaticWeighting(h=" + hw + ", w=" + mw + ')';
    }
}
