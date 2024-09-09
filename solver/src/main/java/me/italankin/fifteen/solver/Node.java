package me.italankin.fifteen.solver;

import kotlin.collections.CollectionsKt;
import me.italankin.fifteen.game.Game;
import me.italankin.fifteen.game.Utils;
import me.italankin.fifteen.solver.heuristics.Heuristics;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * A class representing game state for solving
 */
public class Node {

    /**
     * Parent of this node, {@code null} if this node is root
     */
    @Nullable
    public final Node parent;
    /**
     * {@link Heuristics} value of this node
     */
    public final int heuristicsValue;

    /**
     * State of the game
     */
    public final int[] state;
    /**
     * Number of moves (depth) of this node
     */
    public final int moves;
    /**
     * Index of <code>0</code> in {@link #state}
     */
    public final int zeroIndex;

    public final GameParameters gameParameters;

    public final Heuristics heuristics;

    private final int hashcodeValue;

    public Node(Game game, Heuristics heuristics) {
        this(game, new GameParameters(game), heuristics);
    }

    public Node(Game game, GameParameters params, Heuristics heuristics) {
        this.state = CollectionsKt.toIntArray(game.getState());
        this.gameParameters = params;
        this.moves = 0;
        this.parent = null;
        this.zeroIndex = game.getState().indexOf(0);
        this.heuristics = heuristics;
        this.hashcodeValue = Arrays.hashCode(state);
        this.heuristicsValue = heuristics.calc(state, gameParameters);
    }

    protected Node(int[] state,
            int moves,
            Node parent,
            int zeroIndex,
            Heuristics heuristics,
            GameParameters gameParameters) {
        this.state = state;
        this.gameParameters = gameParameters;
        this.moves = moves;
        this.parent = parent;
        this.zeroIndex = zeroIndex;
        this.heuristics = heuristics;
        this.hashcodeValue = Arrays.hashCode(state);
        this.heuristicsValue = heuristics.calc(
                state,
                gameParameters,
                parent.heuristicsValue,
                parent.zeroIndex,
                zeroIndex);
    }

    /**
     * Get children of this node. <b>DOES NOT</b> return the parent node.
     * <br/>
     * Because the parent of this node is also a child (graph is undirected), including parent here will result in
     * double-checking the same position twice.
     *
     * @return child nodes, excluding {@link #parent}; may contain {@code null}s
     */
    public Node[] children() {
        int width = gameParameters.width;
        Node[] nodes = new Node[parent == null ? 4 : 3]; // only for the root node we need 4 elements
        int parentZeroIndex = parent != null ? parent.zeroIndex : -1;
        int down = zeroIndex + width;
        int idx = 0;
        if (down < gameParameters.size && down != parentZeroIndex) {
            nodes[idx++] = newMove(down);
        }
        int up = zeroIndex - width;
        if (up >= 0 && up != parentZeroIndex) {
            nodes[idx++] = newMove(up);
        }
        if (zeroIndex % width > 0) {
            int left = zeroIndex - 1;
            if (left != parentZeroIndex) {
                nodes[idx++] = newMove(left);
            }
        }
        if (zeroIndex % width < width - 1) {
            int right = zeroIndex + 1;
            if (right != parentZeroIndex) {
                nodes[idx] = newMove(right);
            }
        }
        return nodes;
    }

    public List<Node> getPath() {
        int size = moves + 1;
        Node[] nodes = new Node[size];
        Node p = this;
        do {
            nodes[--size] = p;
        } while ((p = p.parent) != null);
        return Arrays.asList(nodes);
    }

    public boolean isGoal() {
        return Arrays.equals(state, gameParameters.goal);
    }

    /**
     * @return the number that was moved to get this state, or {@code -1} if it's the first node
     */
    public int lastMovedNumber() {
        if (parent == null) {
            return -1;
        }
        return state[parent.zeroIndex];
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node another) {
            // equal positions have equal heuristics
            // optimize to avoid comparing arrays
            if (heuristicsValue != another.heuristicsValue) {
                return false;
            }
            for (int i = 0; i < state.length; i++) {
                if (state[i] != another.state[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashcodeValue;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        int padding = 2;
        int cellWidth = String.valueOf(gameParameters.size).length() + padding;
        char delimiter = ' ';

        int width = gameParameters.width;

        StringBuilder info = new StringBuilder();
        info.append(" h=");
        info.append(heuristicsValue);
        info.append(" m=");
        info.append(moves);
        info.append(" i=");
        info.append(Utils.inversions(state));
        info.append(' ');

        int sepSymbols = Math.max(cellWidth * width + padding, info.length());
        StringBuilder separator = new StringBuilder(sepSymbols);
        separator.append('+');
        separator.append("-".repeat(Math.max(0, sepSymbols)));
        separator.append('+');

        result.append(separator);
        result.append("\n|");
        result.append(info);
        result.append(" ".repeat(Math.max(0, sepSymbols - info.length())));
        result.append("|\n");
        result.append(separator);
        result.append("\n");

        for (int i = 0, s = state.length; i < s; i++) {
            if (i % width == 0) {
                result.append('|');
            }
            int number = state[i];
            appendNum(result, number, cellWidth, delimiter);
            if ((i + 1) % width == 0) {
                result.append(" ".repeat(Math.max(0, sepSymbols - cellWidth * width)));
                result.append("|\n");
            }
        }
        result.append(separator);
        return result.toString();
    }

    protected Node newMove(int index) {
        int[] newState = new int[state.length];
        System.arraycopy(state, 0, newState, 0, state.length);
        // NB: does not perform move validity
        int p = newState[index];
        newState[index] = newState[zeroIndex];
        newState[zeroIndex] = p;
        return newChildNode(newState, moves + 1, this, index, heuristics, gameParameters);
    }

    protected Node newChildNode(int[] newState, int moves, Node parent, int zeroIndex, Heuristics heuristics, GameParameters gameParameters) {
        return new Node(newState, moves, parent, zeroIndex, heuristics, gameParameters);
    }

    private static void appendNum(StringBuilder sb, int num, int cellWidth, char delimiter) {
        String s = String.valueOf(num);
        for (int i = 0, c = cellWidth - s.length(); i < c; i++) {
            sb.append(delimiter);
        }
        sb.append(num == 0 ? "-" : s);
    }
}
