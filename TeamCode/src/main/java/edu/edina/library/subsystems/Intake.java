package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.CurrentOperation;
import edu.edina.library.util.RobotState;
import edu.edina.opmodes.teleop.Robot;

public class Intake extends Subsystem {
    private DcMotorEx slideMotor;
    private DcMotorEx flipMotor;
    private Servo flipServo;
    private CRServo intakeServo;
    private double intakespeed;
    private RobotState robotState;
    private boolean successfulSetup;

    public Intake(HardwareMap map, RobotState robotState){
        try {
            slideMotor = map.get(DcMotorEx.class, "slideMotor");
            flipMotor = map.get(DcMotorEx.class, "flipMotor");
            flipServo = map.get(Servo.class, "flipServo");
            intakeServo = map.get(CRServo.class, "intakeServo");

            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flipMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            successfulSetup = true;
        } catch (Exception ex) {
            successfulSetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (successfulSetup) {
            if (robotState.CurrentOperation == CurrentOperation.Running) {
                slideMotor.setPower(intakespeed);
            } else if (robotState.CurrentOperation == CurrentOperation.Intake) {
                if (slideMotor.getCurrentPosition() < 100) {
                    slideMotor.setPower(0);
                }
            }
        }
    }

    public void SetSpeed(double lefttrigger, double righttrigger){
        if (righttrigger != 0 && lefttrigger!= 0) {
            intakespeed = 0;
        } else if (righttrigger != 0) {
            intakespeed = righttrigger;
        } else if (lefttrigger != 0) {
            intakespeed = lefttrigger;
        } else {
            intakespeed = 0;
        }
    }

    @Override
    public void telemetry(Telemetry telemetry) {
        if (successfulSetup) {
            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Arm Position", flipMotor.getCurrentPosition());
            telemetry.addData("Arm Servo Position", flipServo.getPosition());
            telemetry.addData("Intake Servo Speed", intakeServo.getPower());
        } else {
            telemetry.addData("Unable to setup motors slideMotor or flipMotor or setup servos flipServo or intakeServo", "");
        }
    }
}
