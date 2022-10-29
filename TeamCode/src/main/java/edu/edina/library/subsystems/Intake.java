package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.opmodes.teleop.Robot;
import edu.edina.opmodes.teleop.RobotState;

public class Intake extends Subsystem{

    private DcMotorEx rightmotor;
    private DcMotorEx leftmotor;
    private DcMotorEx flipMotor;
    private Servo flipServo;
    private CRServo intakeServo;
    private double intakespeed;
    private Robot robot;

    public Intake(HardwareMap map, Robot robot){
        rightmotor = map.get(DcMotorEx.class, "rightmotor");
        leftmotor = map.get(DcMotorEx.class, "leftmotor");
        this.robot = robot;
    }

    @Override
    public void update() {


        if(robot.robotState == RobotState.Running)
        {
            rightmotor.setPower(intakespeed);
            leftmotor.setPower(intakespeed);
        } else if(robot.robotState == RobotState.Intake)
        {
            if(leftmotor.getCurrentPosition() > 100)
            {
                rightmotor.setPower(1);
                leftmotor.setPower(1);
            } else
            {
                    rightmotor.setPower(0);
                    leftmotor.setPower(0);13
            }
            if(leftmotor.getCurrentPosition() < 800)
            {
               flipServo.setPosition(-1);
            } else
            {
                flipServo.setPosition(1);
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
