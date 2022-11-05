package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.ClawServoPosition;
import edu.edina.library.util.PoleLocation;
import edu.edina.library.util.RobotState;

public class Lift extends Subsystem {

    private DcMotorEx liftMotor;
    private RobotState robotState;
    private Servo liftServo;
    private Servo latchServo;
    private double liftSpeed;
    private double armPosition;
    private long lastUpdate;

    public Lift(HardwareMap map, RobotState robotState) {
        try {
            liftMotor = map.get(DcMotorEx.class, "liftMotor");
            liftServo = map.get(Servo.class, "liftArmServo");
            latchServo = map.get(Servo.class, "latchServo");

            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robotState.LiftSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.LiftSuccessfullySetup = false;
        }

        armPosition = 1;
        lastUpdate = System.currentTimeMillis();
        robotState.ClawServoPosition = ClawServoPosition.Closed;
        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.ClawServoPosition == ClawServoPosition.Open) {
            latchServo.setPosition(.8);
        } else {
            latchServo.setPosition(.6);
        }

        liftMotor.setPower(liftSpeed);

        robotState.LatchServoLocation = -.4 * armPosition + .5;
        liftServo.setPosition(robotState.LatchServoLocation);
    }

    public void setLiftProperties(double liftSpeed, double armPosition, boolean latchOpen,
                                  boolean lowPole, boolean mediumPole, boolean highPole) {
        this.liftSpeed = liftSpeed;
        if (System.currentTimeMillis() > (lastUpdate + 25)) {
            if (armPosition > 0) {
                this.armPosition += .05;
            } else if (armPosition < 0) {
                this.armPosition -= .05;
            }

            this.armPosition = Math.min(1, Math.max(-1, this.armPosition));
            lastUpdate = System.currentTimeMillis();
        }

        if (latchOpen) {
            robotState.ClawServoPosition = ClawServoPosition.Open;
        } else {
            robotState.ClawServoPosition = ClawServoPosition.Closed;
        }

        if (lowPole) {
            robotState.TargetPoleLocation = PoleLocation.Low;
        } else if (mediumPole) {
            robotState.TargetPoleLocation = PoleLocation.Medium;
        } else if (highPole) {
            robotState.TargetPoleLocation = PoleLocation.High;
        }
    }
}
