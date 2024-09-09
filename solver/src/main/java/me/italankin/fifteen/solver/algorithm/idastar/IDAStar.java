package me.italankin.fifteen.solver.algorithm.idastar;

import me.italankin.fifteen.solver.Node;
import me.italankin.fifteen.solver.algorithm.Algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Iterative_deepening_A*">Wikipedia</a>
 */
public class IDAStar implements Algorithm {

    private static final int FOUND = -1;

    @Override
    public Result run(Node start) {
        AtomicInteger visitedNodes = new AtomicInteger();
        int threshold = start.heuristicsValue;
        Deque<Node> path = new ArrayDeque<>(64);
        path.add(start);
        while (true) {
            int t = search(path, 0, threshold, visitedNodes);
            if (t == FOUND) {
                return new Result(path.getLast(), visitedNodes.get(), 0);
            }
            threshold = t;
        }
    }

    private static int search(Deque<Node> path, int g, int threshold, AtomicInteger visitedNodes) {
        Node node = path.getLast();
        visitedNodes.incrementAndGet();
        int f = g + node.heuristicsValue;
        if (f > threshold) {
            return f;
        }
        if (node.isGoal()) {
            return FOUND;
        }
        int min = Integer.MAX_VALUE;
        for (Node child : node.children()) {
            if (child == null) break;
            if (!path.contains(child)) {
                path.addLast(child);
                int t = search(path, g + 1, threshold, visitedNodes);
                if (t == FOUND) {
                    return FOUND;
                }
                if (min > t) {
                    min = t;
                }
                path.removeLast();
            }
        }
        return min;
    }

    public String toString() {
        return "IDA*";
    }
}
