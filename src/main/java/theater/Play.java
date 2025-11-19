package theater;

/**
 * Class representing a play.
 */
public class Play {

    private final String name;
    private final String type;

    /**
     * Creates a Play.
     *
     * @param name name of the play
     * @param type type of the play (e.g., tragedy, comedy)
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
