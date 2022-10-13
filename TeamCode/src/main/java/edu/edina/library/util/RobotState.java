package edu.edina.library.util;

public class RobotState {
    public CurrentOperation CurrentOperation;

    public long SlideLocation = 0;
    public long LiftLocation = 0;
    public long ArmLocation = 0;

    public LiftServoLocation LiftServoLocation;
    public ArmServoLocation ArmServoLocation;
    public LatchServoPosition ClampServoPosition;
}
