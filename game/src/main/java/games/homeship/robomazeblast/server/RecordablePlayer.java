package games.homeship.robomazeblast.server;

import games.homeship.robomazeblast.server.api.Element;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RecordablePlayer extends Player {
    // Action constants
    public static final int ACTION_NONE = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_DOWN = 2;
    public static final int ACTION_LEFT = 3;
    public static final int ACTION_RIGHT = 4;
    public static final int ACTION_BOMB = 5;

    private final List<String> recordings = new ArrayList<>();
    private boolean isRecording = false;

    public RecordablePlayer(Game game, String nickname) {
        super(game, nickname);
    }

    @Override
    void move(int dx, int dy) {
        super.move(dx, dy);
        if (isRecording) {
            recordMovement(dx, dy);
        }
    }

    @Override
    protected void placeBomb() {
        super.placeBomb();
        if (isRecording) {
            recordAction(ACTION_BOMB);
        }
    }

    private void recordMovement(int dx, int dy) {
        int action = ACTION_NONE;
        if (dx == 0 && dy == -1) action = ACTION_UP;
        else if (dx == 0 && dy == 1) action = ACTION_DOWN;
        else if (dx == -1 && dy == 0) action = ACTION_LEFT;
        else if (dx == 1 && dy == 0) action = ACTION_RIGHT;

        recordAction(action);
    }

    private void recordAction(int action) {
        double[] state = getStateVector();
        String record = String.format("%.2f,%.2f,%.1f,%.1f,%.2f,%d",
                state[0], state[1], state[2], state[3], state[4], action);
        recordings.add(record);
    }

    private double[] getStateVector() {
        Playground pg = this.game.getPlayground();
        // Normalized position [0-1]
        double normX = gridX / (double) pg.getColumns();
        double normY = gridY / (double) pg.getRows();

        // Bomb availability
        double bombAvailable = (bombs.size() < this.bombCount) ? 1.0 : 0.0;

        // Bomb proximity (using simplified check)
        double bombNearby = isBombNearby() ? 1.0 : 0.0;
        double bombDirection = bombDirectionScore();

        return new double[]{normX, normY, bombAvailable, bombNearby, bombDirection};
    }

    private boolean isBombNearby() {
        // Simplified version of AIPlayer's checkForBomb
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                Element[] el = game.getPlayground().getElement(gridX + i, gridY + j);
                if (el != null && el.length > 0 && el[0] instanceof Bomb) {
                    return true;
                }
            }
        }
        return false;
    }

    private double bombDirectionScore() {
        // Simplified directional threat calculation
        for (int i = 1; i <= 3; i++) {
            // Check horizontal
            Element[] right = game.getPlayground().getElement(gridX + i, gridY);
            if (right != null && right.length > 0 && right[0] instanceof Bomb)
                return 1.0 - (i * 0.2);

            Element[] left = game.getPlayground().getElement(gridX - i, gridY);
            if (left != null && left.length > 0 && left[0] instanceof Bomb)
                return -1.0 + (i * 0.2);

            // Check vertical
            Element[] down = game.getPlayground().getElement(gridX, gridY + i);
            if (down != null && down.length > 0 && down[0] instanceof Bomb)
                return 0.5 - (i * 0.1);

            Element[] up = game.getPlayground().getElement(gridX, gridY - i);
            if (up != null && up.length > 0 && up[0] instanceof Bomb)
                return -0.5 + (i * 0.1);
        }
        return 0.0;
    }

    public void startRecording() {
        isRecording = true;
        recordings.clear();
    }

    public void stopRecording() {
        isRecording = false;
    }

    public void saveRecordings(String filename) {
        try (PrintWriter out = new PrintWriter(filename)) {
            recordings.forEach(out::println);
        } catch (Exception e) {
            System.err.println("Error saving recordings: " + e.getMessage());
        }
    }
}