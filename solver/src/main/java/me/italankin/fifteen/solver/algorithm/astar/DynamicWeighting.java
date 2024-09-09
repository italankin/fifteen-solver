package me.italankin.fifteen.solver.algorithm.astar;

import me.italankin.fifteen.solver.Node;

import java.util.Comparator;

public class DynamicWeighting implements Comparator<Node> {

    private final float e;
    private final float N;

    /**
     * @param e relaxation coefficient
     * @param N anticipated solution length
     */
    public DynamicWeighting(float e, int N) {
        if (e < 0) {
            throw new IllegalArgumentException("e must be >= 0");
        }
        if (N < 1) {
            throw new IllegalArgumentException("N must be >= 1");
        }
        this.e = e;
        this.N = N;
    }

    @Override
    public int compare(Node lhs, Node rhs) {
        return Float.compare(cost(lhs), cost(rhs));
    }

    private float cost(Node node) {
        float w = (node.moves <= N) ? (1f - node.moves / N) : 0f;
        return node.moves + (1 + e * w) * node.heuristicsValue;
    }

    @Override
    public String toString() {
        return "DynamicWeighting(e=" + e + ", N=" + N + ")";
    }
}
