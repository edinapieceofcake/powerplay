package edu.edina.library.util;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotState {
    public CurrentOperation CurrentOperation;

    public DriveSpeed DriveSpeed = edu.edina.library.util.DriveSpeed.Medium;

    public long SlideMotorLocation = 0;
    public long SlideArmMotorLocation = 0;
    public double SlideArmServoLocation = 0;
    public SlideMotorAction SlideMotorAction = edu.edina.library.util.SlideMotorAction.Idle;
    public IntakeServoAction IntakeServoAction = edu.edina.library.util.IntakeServoAction.Idle;
    public SlideArmMotorAction SlideArmMotorAction = edu.edina.library.util.SlideArmMotorAction.Idle;
    public SlideZone SlideZone = edu.edina.library.util.SlideZone.AllowFullMovement;
    public IntakeArmZone ArmZone = edu.edina.library.util.IntakeArmZone.AllowFullMovement;

    public double LatchServoLocation = 0;
    public double LiftArmServoLocation = 0;
    public double LiftSpeed = 0;
    public long LiftMotorLocation = 0;
    public LatchServoPosition LatchServoPosition = edu.edina.library.util.LatchServoPosition.Closed;
    public PoleLocation CurrentPoleLocation = edu.edina.library.util.PoleLocation.None;
    public PoleLocation TargetPoleLocation = edu.edina.library.util.PoleLocation.None;
    public LiftZone LiftZone = edu.edina.library.util.LiftZone.AllowFullMovement;
    public LiftArmZone LiftArmZone = edu.edina.library.util.LiftArmZone.AllowFullMovement;

    public boolean IntakeSuccessfullySetup = false;
    public boolean LiftSuccessfullySetup = false;
    public boolean DriveSuccessfullySetup = false;

    public RobotState() {}

    public void telemetry(Telemetry telemetry) {
        if (IntakeSuccessfullySetup) {
            telemetry.addData("Slide Position", SlideMotorLocation);
            telemetry.addData("Arm Position", SlideArmMotorLocation);
            telemetry.addData("Arm Servo Position", SlideArmServoLocation);
        } else {
            telemetry.addData("Unable to setup motors slideMotor or flipMotor or setup servos flipServo or intakeServo", "");
        }

        if (LiftSuccessfullySetup) {
            telemetry.addData("Lift Position", LiftMotorLocation);
            telemetry.addData("Lift Arm Servo Position", LiftArmServoLocation);
            telemetry.addData("Latch Servo Location", LatchServoLocation);
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
