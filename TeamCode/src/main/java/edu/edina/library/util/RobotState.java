package edu.edina.library.util;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotState {
    public CurrentOperation CurrentOperation;

    public DriveSpeed DriveSpeed = edu.edina.library.util.DriveSpeed.Medium;

    public long SlideMotorLocation = 0;
    public long IntakeMotorLocation = 0;
    public double SlideArmServoLocation = 0;
    public boolean SlideIntakeSwitch = false;
    public boolean SlideSwitch = false;
    public boolean ArmSwitch = false;
    public SlideMotorAction SlideMotorAction = edu.edina.library.util.SlideMotorAction.Idle;
    public IntakeServoAction IntakeServoAction = edu.edina.library.util.IntakeServoAction.Idle;
    public SlideArmMotorAction SlideArmMotorAction = edu.edina.library.util.SlideArmMotorAction.Idle;
    public boolean AutoFoldInArm = false;
    public boolean AutoFoldOutArm = false;
    public boolean DroppedOffCone = false;
    public long DroppedOfftime;
    public boolean IntakeClampOpen;
    public double FlipPosition = 0.45;

    public double LatchServoLocation = 0;
    public long LiftDiff;
    public long LiftMotorLocation = 0;
    public ClawServoPosition ClawServoPosition = edu.edina.library.util.ClawServoPosition.Closed;
    public ElbowServoPosition ElbowServoPosition = edu.edina.library.util.ElbowServoPosition.In;
    public LiftFilpServoPosition LiftFilpServoPosition = edu.edina.library.util.LiftFilpServoPosition.Middle;
    public PoleLocation TargetPoleLocation = edu.edina.library.util.PoleLocation.None;
    public double ClawPosition = 0.0;
    public double ElbowPosition = 0.0;
    public double LiftFlipPosition = 0.0;

    public boolean IntakeSuccessfullySetup = false;
    public boolean LiftSuccessfullySetup = false;
    public boolean DriveSuccessfullySetup = false;

    public RobotState() {}

    public void telemetry(Telemetry telemetry) {
        if (IntakeSuccessfullySetup) {
            telemetry.addData("Slide Position", SlideMotorLocation);
            telemetry.addData("Folding Arm In", AutoFoldInArm);
            telemetry.addData("Dropped off time", DroppedOfftime);
            telemetry.addData("Dropped off cone", DroppedOffCone);
            telemetry.addData("IntakeClampOpen", IntakeClampOpen);
            telemetry.addData("FlipPosition", FlipPosition);
        } else {
            telemetry.addData("Unable to setup motors slideMotor or flipMotor or setup servos flipServo or intakeServo", "");
        }

        if (LiftSuccessfullySetup) {
            telemetry.addData("Lift Position", LiftMotorLocation);
            telemetry.addData("ClawPosition", ClawPosition);
            telemetry.addData("ElbowPosition", ElbowPosition);
            telemetry.addData("LiftFlipPosition", LiftFlipPosition);
            telemetry.addData("LiftDiff", LiftDiff);
        } else {
            telemetry.addData("Unable to setup motors liftMotor or setup servos armServo or latchServo", "");
        }

        if (DriveSuccessfullySetup) {
            telemetry.addData("Drive Speed", DriveSpeed);
        } else {
            telemetry.addData("MecanumDrive: Unable to setup frontLeft, frontRight, backLeft, backRight motors", "");
        }
    }
}
