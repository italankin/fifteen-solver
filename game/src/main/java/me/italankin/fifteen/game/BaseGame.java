package me.italankin.fifteen.game;

import java.util.*;

public class BaseGame implements Game {

    public static boolean isSolvable(List<Integer> state, List<Integer> goal, int width) {
        int stateInversions = Utils.inversions(state);
        int goalInversions = Utils.inversions(goal);
        if (width % 2 == 0) {
            int goalZeroRowIndex = goal.indexOf(0) / width;
            int startZeroRowIndex = state.indexOf(0) / width;
            // if 'goalInversions' is even
            // 'stateInversions' and difference between 'goal' zero row index and zero row index in the 'state'
            // should have the same parity
            // if 'goalInversions' is odd, 'stateInversions' and difference must have different parity

            // since we're interested only in parity, an optimization is possible
            return goalInversions % 2 == (stateInversions + goalZeroRowIndex + startZeroRowIndex) % 2;
        }
        // 'startInversions' should have the same parity as 'goalInversions'
        return stateInversions % 2 == goalInversions % 2;
    }

    protected static final Random RANDOM = new Random();

    protected final int width;
    protected final int height;
    protected final List<Integer> state;
    protected final List<Integer> goal;

    protected int moves;
    protected boolean solved;

    protected final List<StateCallback> stateCallbacks = new ArrayList<>(1);

    public BaseGame(int width, int height, List<Integer> goal, boolean randomMissingTile, Scrambler scrambler) {
        this(width, height, goal, getMissingTile(width, height, randomMissingTile), scrambler);
    }

    public BaseGame(int width, int height, List<Integer> goal, int missingTile, Scrambler scrambler) {
        this.width = width;
        this.height = height;
        this.goal = new ArrayList<>(goal);

        int size = width * height;
        if (missingTile != size) {
            this.goal.set(this.goal.indexOf(0), size);
            int missingTileIndex = this.goal.indexOf(missingTile);
            if (missingTileIndex == -1) {
                throw new IllegalArgumentException(
                        "missingTile=" + missingTile + " is not part of the goal=" + goal);
            }
            this.goal.set(missingTileIndex, 0);
        }

        this.state = initState(missingTile, scrambler);
        this.solved = this.state.equals(this.goal);
    }

    public BaseGame(int width, int height, List<Integer> state, List<Integer> goal) {
        this(width, height, state, goal, 0);
    }

    public BaseGame(int width,
            int height,
            List<Integer> state,
            List<Integer> goal,
            int moves) {
        if (state.size() != goal.size()) {
            throw new IllegalArgumentException("state and goal size does not match");
        }
        if (goal.size() != width * height) {
            throw new IllegalArgumentException("Invalid size: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        this.moves = moves;
        this.goal = new ArrayList<>(goal);
        this.state = new ArrayList<>(state);

        Set<Integer> numbers = new HashSet<>(state);
        int size = width * height;
        for (int i = 0; i < size; i++) {
            int number = i + 1;
            if (!numbers.contains(number)) {
                this.goal.set(this.goal.indexOf(0), size);
                this.goal.set(this.goal.indexOf(number), 0);
                break;
            }
        }

        this.solved = this.state.equals(this.goal);

        if (!isSolvable(this.state, this.goal, this.width)) {
            throw new IllegalStateException("goal=" + goal + " is unreachable from state=" + state);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int move(int x, int y) {
        return move(y * width + x);
    }

    @Override
    public int move(int index) {
        return move(index, true);
    }

    @Override
    public List<Integer> move(List<Integer> indices) {
        if (indices.isEmpty()) {
            return indices;
        }
        boolean moved = false;
        List<Integer> newIndices = new ArrayList<>(indices.size());
        Direction direction = getDirection(indices.get(0));
        for (int oldIndex : indices) {
            if (direction != getDirection(oldIndex)) {
                break;
            }
            int newIndex = move(oldIndex, false);
            newIndices.add(newIndex);
            moved |= oldIndex != newIndex;
        }
        if (moved) {
            moves++;
            solved = state.equals(goal);
            int[] numbers = new int[newIndices.size()];
            for (int i = 0; i < newIndices.size(); i++) {
                numbers[i] = state.get(newIndices.get(i));
            }
            for (int i = stateCallbacks.size() - 1; i >= 0; i--) {
                StateCallback stateCallback = stateCallbacks.get(i);
                stateCallback.onTilesMoved(this, numbers);
                stateCallback.onGameStateUpdated(this);
            }
        }
        return newIndices;
    }

    @Override
    public List<Integer> findMovingTiles(int startIndex, Direction direction) {
        if (startIndex < 0) {
            // maybe we're outside the universe
            return Collections.emptyList();
        }

        int x, y;

        int x1 = startIndex % width;
        int y1 = startIndex / width;

        int zeroPos = state.indexOf(0);
        int x0 = zeroPos % width;
        int y0 = zeroPos / width;

        ArrayList<Integer> result = new ArrayList<>();

        if (direction == Direction.DEFAULT) {
            direction = getDirection(startIndex);
        }

        switch (direction) {
            case UP:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 + 1; y < Math.min(height, y1 + 1); y++) {
                    result.add(y * width + x0);
                }
                break;

            case RIGHT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 - 1; x >= x1; x--) {
                    result.add(y0 * width + x);
                }
                break;

            case DOWN:
                // check we're moving tiles in the same column
                if (x1 != x0) {
                    break;
                }
                for (y = y0 - 1; y >= y1; y--) {
                    result.add(y * width + x0);
                }
                break;

            case LEFT:
                // check we're moving tiles in the same row
                if (y1 != y0) {
                    break;
                }
                for (x = x0 + 1; x < Math.min(width, x1 + 1); x++) {
                    result.add(y0 * width + x);
                }
                break;
        }

        return result;
    }

    @Override
    public List<Integer> possibleMoves() {
        int zeroPos = state.indexOf(0);
        int zx = zeroPos % width;
        int zy = zeroPos / width;
        List<Integer> result = new ArrayList<>(4);
        if (zx > 0) {
            result.add(zy * width + (zx - 1));
        }
        if (zx < width - 1) {
            result.add(zy * width + (zx + 1));
        }
        if (zy > 1) {
            result.add((zy - 1) * width + zx);
        }
        if (zy < height - 1) {
            result.add((zy + 1) * width + zx);
        }
        return result;
    }

    @Override
    public List<Integer> getState() {
        return state;
    }

    @Override
    public List<Integer> getGoal() {
        return goal;
    }

    @Override
    public Direction getDirection(int index) {
        int zeroPos = state.indexOf(0);
        int dx = zeroPos % width - index % width;
        int dy = zeroPos / width - index / width;
        return Game.Direction.of(dx, dy);
    }

    @Override
    public int getMoves() {
        return moves;
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    @Override
    public int getSize() {
        return state.size();
    }

    @Override
    public void addStateCallback(StateCallback callback) {
        if (!stateCallbacks.contains(callback)) {
            stateCallbacks.add(callback);
        }
    }

    @Override
    public void removeStateCallback(StateCallback callback) {
        stateCallbacks.remove(callback);
    }

    private int move(int index, boolean updateState) {
        int zeroIndex = state.indexOf(0);
        if (index == zeroIndex) {
            return index;
        }

        // if distance to zero is not 1, we can't move
        if (Utils.manhattan(zeroIndex, index, width) != 1) {
            return index;
        }

        Collections.swap(state, index, zeroIndex);
        if (updateState) {
            moves++;
            solved = state.equals(goal);
            for (int i = stateCallbacks.size() - 1; i >= 0; i--) {
                StateCallback stateCallback = stateCallbacks.get(i);
                stateCallback.onTileMoved(this, state.get(zeroIndex));
                stateCallback.onGameStateUpdated(this);
            }
        }

        return zeroIndex;
    }

    private List<Integer> initState(int missingTile, Scrambler scrambler) {
        List<Integer> result = new ArrayList<>(goal);
        scrambler.scramble(width, Collections.unmodifiableList(goal), result);
        for (int i : goal) {
            if (!result.contains(i)) {
                throw new IllegalStateException("Missing " + i + " in start state! state=" + result + ", goal=" + goal);
            }
        }

        if (!isSolvable(result, goal, width)) {
            // if puzzle is not solvable
            // we swap last two digits (e.g. 14 and 15)

            int size = height * width;
            int last, secondLast;
            if (missingTile == size) {
                last = size - 1;
                secondLast = size - 2;
            } else {
                last = size;
                secondLast = last - 1;
                if (missingTile == secondLast) {
                    secondLast = missingTile - 1;
                }
            }
            Collections.swap(result, result.indexOf(last), result.indexOf(secondLast));
        }
        return result;
    }

    private static int getMissingTile(int width, int height, boolean randomMissingTile) {
        return randomMissingTile ? (1 + RANDOM.nextInt(width * height)) : width * height;
    }
}
