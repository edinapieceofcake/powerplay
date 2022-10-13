package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.RobotState;

public class Lift extends Subsystem {

    private DcMotorEx liftMotor;
    private double liftSpeed;
    private RobotState robotState;
    private Servo armServo;
    private Servo latchServo;
    private boolean successfulSetup;

    public Lift(HardwareMap map, RobotState robotState) {
        try {
            liftMotor = map.get(DcMotorEx.class, "liftMotor");
            armServo = map.get(Servo.class, "armServo");
            latchServo = map.get(Servo.class, "latchServo");

            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            successfulSetup = true;
        } catch (Exception ex) {
            successfulSetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (successfulSetup) {
            liftMotor.setPower(liftSpeed);
        }
    }

    public void setSpeed(double liftSpeed) {
        this.liftSpeed = liftSpeed;
    }

    @Override
    public void telemetry(Telemetry telemetry) {
        if (successfulSetup) {
            telemetry.addData("Lift Position", liftMotor.getCurrentPosition());
            telemetry.addData("Arm Servo Position", armServo.getPosition());
            telemetry.addData("Latch Servo Location", latchServo.getPosition());
        } else {
            telemetry.addData("Unable to setup motors liftMotor or setup servos armServo or latchServo", "");
        }
    }
}
