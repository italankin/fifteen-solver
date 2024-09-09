package me.italankin.fifteen.solver.algorithm.astar;

import me.italankin.fifteen.solver.Node;
import me.italankin.fifteen.solver.algorithm.Algorithm;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">Wikipedia</a>
 */
public class AStar implements Algorithm {

    private final Comparator<Node> nodeComparator;

    public AStar() {
        this(new DefaultNodeComparator());
    }

    /**
     * @param nodeComparator comparator used for prioritizing nodes for analysis
     */
    public AStar(Comparator<Node> nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    @Override
    public Result run(Node start) {
        // use hashCode values to lower memory consumption
        IntHashSet explored = new IntHashSet();
        int initialCapacity = 1 << (start.gameParameters.size - 2);
        PriorityQueue<Node> queue = new PriorityQueue<>(initialCapacity, nodeComparator);
        queue.add(start);
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            explored.add(node.hashCode());
            if (node.isGoal()) {
                return new Result(node, explored.size(), queue.size());
            }
            for (Node child : node.children()) {
                if (child == null) break;
                if (!explored.contains(child.hashCode())) {
                    queue.add(child);
                }
            }
        }
        throw new IllegalStateException("No solution! " + explored.size() + " nodes explored");
    }

    public String toString() {
        return "A*(" + nodeComparator.toString() + ")";
    }

    private static class DefaultNodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node lhs, Node rhs) {
            return Integer.compare(lhs.heuristicsValue + lhs.moves, rhs.heuristicsValue + rhs.moves);
        }

        @Override
        public String toString() {
            return "Default";
        }
    }
}
