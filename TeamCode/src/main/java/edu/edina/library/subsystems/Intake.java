package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.opmodes.teleop.Robot;
import edu.edina.opmodes.teleop.RobotState;

public class Intake extends Subsystem {
    private DcMotorEx rightmotor;
    private DcMotorEx leftmotor;
    private DcMotorEx flipmotor;
    private Servo flipservo;
    private CRServo intakeservo;
    private Robot robot;
    private double intakespeed;


    public Intake(HardwareMap map, Robot robot){
        rightmotor = map.get(DcMotorEx.class, "rightmotor");
        leftmotor = map.get(DcMotorEx.class, "leftmotor");
        flipmotor = map.get(DcMotorEx.class, "flipmotor");
        flipservo = map.get(Servo.class, "flipservo");
        intakeservo = map.get(CRServo.class, "intakeservo");
        this.robot = robot;
    }

    @Override
    public void update() {
        if (robot.RobotState == RobotState.Running) {
            rightmotor.setPower(intakespeed);
            leftmotor.setPower(intakespeed);
        } else if (robot.RobotState == RobotState.Intake) {
            if (leftmotor.getCurrentPosition() < 100) {
                rightmotor.setPower(0);
                leftmotor.setPower(0);
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

}
