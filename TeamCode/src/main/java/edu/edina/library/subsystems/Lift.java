package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.LatchServoPosition;
import edu.edina.library.util.PoleLocation;
import edu.edina.library.util.RobotState;

public class Lift extends Subsystem {

    private DcMotorEx liftMotor;
    private RobotState robotState;
    private Servo liftServo;
    private Servo latchServo;

    public Lift(HardwareMap map, RobotState robotState) {
        try {
            liftMotor = map.get(DcMotorEx.class, "liftMotor");
            liftServo = map.get(Servo.class, "liftServo");
            latchServo = map.get(Servo.class, "latchServo");

            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robotState.LiftSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.LiftSuccessfullySetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.LiftSuccessfullySetup) {
            if (robotState.LiftSpeed != 0) {
                // we have manual control
                if (robotState.TargetPoleLocation != PoleLocation.None)
                {
                    // we were moving to a location and took over so stop that
                    liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    robotState.TargetPoleLocation = PoleLocation.None;
                }

                liftMotor.setPower(robotState.LiftSpeed);
            } else {
                // check and see if we should be running to a pole location
                if (robotState.TargetPoleLocation != PoleLocation.None) {
                    if (liftMotor.isBusy()) {
                        // looks like we are trying to get someplace
                    } else if (liftMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                        // setup the motor
                        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        liftMotor.setPower(.5);
                        if (robotState.TargetPoleLocation == PoleLocation.Low) {
                            liftMotor.setTargetPosition(100);
                        } else if (robotState.TargetPoleLocation == PoleLocation.Medium) {
                            liftMotor.setTargetPosition(300);
                        } else if (robotState.TargetPoleLocation == PoleLocation.High) {
                            liftMotor.setTargetPosition(500);
                        }
                    } else {
                        robotState.TargetPoleLocation = PoleLocation.None;
                    }
                }
            }

            liftServo.setPosition(robotState.LiftArmServoLocation);
            if (robotState.LatchServoPosition == LatchServoPosition.Open) {
                latchServo.setPosition(1);
            } else {
                latchServo.setPosition(-1);
            }

            robotState.LiftMotorLocation = liftMotor.getCurrentPosition();
            robotState.LatchServoLocation = liftServo.getPosition();
        }
    }

    public void setLiftProperties(double liftSpeed, double armPosition, boolean latchOpen,
                                  boolean lowPole, boolean mediumPole, boolean highPole) {
        if (liftServo.getPosition()>0){
            if (liftMotor.getCurrentPosition()>200){
                robotState.LiftSpeed = liftSpeed;
            } else if (liftSpeed < 0){
                robotState.LiftSpeed = 0;
            } else{
                robotState.LiftSpeed = liftSpeed;
            }
        } else if(robotState.SlideArmMotorLocation < 100){
            robotState.LiftSpeed = 0;
        } else if(liftSpeed < 0){
            robotState.LiftSpeed = 0;
        } else{
            robotState.LiftSpeed = liftSpeed;
        }
        if (liftMotor.getCurrentPosition()<200) {
            robotState.LiftArmServoLocation = 0;
        } else if(armPosition < 0){
            robotState.LiftArmServoLocation = armPosition;
        }else{
            robotState.LiftArmServoLocation = armPosition;
        }
        if (latchOpen) {
            robotState.LatchServoPosition = LatchServoPosition.Open;
        } else {
            robotState.LatchServoPosition = LatchServoPosition.Closed;
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
