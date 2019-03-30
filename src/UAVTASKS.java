public enum UAVTASKS {
    NO_TASK (0),
    FOLLOW_EDGE_CLOCKWISE (1),
    FOLLOW_EDGE_COUNTER_CLOCKWISE (2),
    FLYTHROUGH(3),
    STANDBY(4),
    PATROL(5),
    CHASING_FIRE(6),
    REFUEL(7);

    private final int val;

    UAVTASKS(int val) {
        this.val = val;
    }
}
