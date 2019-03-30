public enum UAVTASKS {
    NO_TASK (0),
    FOLLOW_EDGE_CLOCKWISE (1),
    FOLLOW_EDGE_COUNTER_CLOCKWISE (2),
    FLYTHROUGH(3);

    private final int val;

    UAVTASKS(int val) {
        this.val = val;
    }
}
