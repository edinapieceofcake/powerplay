package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.ClawServoPosition;
import edu.edina.library.util.ElbowServoPosition;
import edu.edina.library.util.LiftFilpServoPosition;
import edu.edina.library.util.PoleLocation;
import edu.edina.library.util.RobotState;

public class Lift2 extends Subsystem {

    private DcMotorEx liftMotor;
    private RobotState robotState;
    private Servo liftFlipServo;
    private Servo elbowServo;
    private Servo clawServo;
    private double liftSpeed;
    private long lastUpdate;
    private boolean runningToPosition;
    private boolean atPosition;
    private boolean armReady;
    private boolean atZeroPosition;
    private int targetPosition = 0;
    private long returnStartedTime = 0;

    public Lift2(HardwareMap map, RobotState robotState) {
        try {
            liftMotor = map.get(DcMotorEx.class, "liftMotor");
            liftFlipServo = map.get(Servo.class, "liftFlipServo");
            elbowServo = map.get(Servo.class, "elbowServo");
            clawServo = map.get(Servo.class, "clawServo");

            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            robotState.ClawServoPosition = ClawServoPosition.Closed;
            robotState.ElbowServoPosition = ElbowServoPosition.Out;
            robotState.LiftFilpServoPosition = LiftFilpServoPosition.Middle;
            robotState.TargetPoleLocation = PoleLocation.None;
            robotState.LiftSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.LiftSuccessfullySetup = false;
        }

        lastUpdate = System.currentTimeMillis();
        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.TargetPoleLocation != PoleLocation.None) {
            if (robotState.TargetPoleLocation == PoleLocation.Return) {
                if (!runningToPosition) {
                    clawServo.setPosition(.45);
                    robotState.ClawServoPosition = ClawServoPosition.Open;
                    liftFlipServo.setPosition(.20);
                    robotState.LiftFilpServoPosition = LiftFilpServoPosition.Pickup;
                    runningToPosition = true;
                    atPosition = false;
                    returnStartedTime = System.currentTimeMillis();
                } else if (!atPosition) {
                    if ((System.currentTimeMillis() > (returnStartedTime + 1000)) && (liftFlipServo.getPosition() == .20)) {
                        atPosition = true;
                        elbowServo.setPosition(.71);
                        robotState.ElbowServoPosition = ElbowServoPosition.In;
                        armReady = false;
                        returnStartedTime = System.currentTimeMillis();
                    }
                } else if (!armReady) {
                    if ((System.currentTimeMillis() > (returnStartedTime + 1000)) &&(elbowServo.getPosition() == .71)) {
                        liftMotor.setTargetPosition(0);
                        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        liftMotor.setPower(.5);
                        atZeroPosition = false;
                        armReady = true;
                    }
                } else if (!atZeroPosition) {
                    int diff = Math.abs(liftMotor.getCurrentPosition());

                    if (diff < 10) {
                        resetState();
                    }
                }
            } else {
                if (!runningToPosition) {
                    if (robotState.TargetPoleLocation == PoleLocation.Low) {
                        targetPosition = -400;
                    } else if (robotState.TargetPoleLocation == PoleLocation.Medium) {
                        targetPosition = -600;
                    } else if (robotState.TargetPoleLocation == PoleLocation.High) {
                        targetPosition = -800;
                    }

                    clawServo.setPosition(.55);
                    robotState.ClawServoPosition = ClawServoPosition.Closed;
                    liftMotor.setTargetPosition(targetPosition);
                    liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    liftMotor.setPower(.5);
                    runningToPosition = true;
                    atPosition = false;
                } else if (!atPosition) {
                    int diff = Math.abs(Math.abs(liftMotor.getCurrentPosition()) - Math.abs(targetPosition));

                    if ((Math.abs(liftMotor.getCurrentPosition()) > 200) && (elbowServo.getPosition() != .6)) {
                        elbowServo.setPosition(.6);
                        robotState.ElbowServoPosition = ElbowServoPosition.Out;
                    }

                    if (diff < 10) {
                        atPosition = true;
                        armReady = false;
                    }
                } else if (!armReady) {
                    liftFlipServo.setPosition(.9);
                    robotState.LiftFilpServoPosition = LiftFilpServoPosition.DropOff;

                    if (robotState.ClawServoPosition == ClawServoPosition.Open) {
                        clawServo.setPosition(.45);
                    } else if (robotState.ClawServoPosition == ClawServoPosition.Closed) {
                        clawServo.setPosition(.55);
                    }
                }
            }
        } else {
            if (robotState.ClawServoPosition == ClawServoPosition.Open) {
                clawServo.setPosition(.45);
            } else if (robotState.ClawServoPosition == ClawServoPosition.Closed) {
                clawServo.setPosition(.55);
            }

            if (robotState.ElbowServoPosition == ElbowServoPosition.In) {
                elbowServo.setPosition(.71);
            } else if (robotState.ElbowServoPosition == ElbowServoPosition.Out) {
                elbowServo.setPosition(.6);
            }

            if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.Pickup) {
                liftFlipServo.setPosition(.20);
            } else if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.DropOff) {
                liftFlipServo.setPosition(.9);
            } else if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.Middle) {
                liftFlipServo.setPosition(.6);
            }

            liftMotor.setPower(liftSpeed);
        }

        robotState.LiftMotorLocation = liftMotor.getCurrentPosition();
        robotState.ClawPosition = clawServo.getPosition();
        robotState.ElbowPosition = elbowServo.getPosition();
        robotState.LiftFlipPosition = liftFlipServo.getPosition();
    }

    public void setLiftProperties(double liftSpeed, boolean elbowOpen, boolean elbowClosed,
                                  boolean latchOpen, boolean latchClosed, boolean intake,
                                  boolean dropOff, boolean lowPole, boolean mediumPole,
                                  boolean highPole, boolean returnPosition) {

        if (robotState.TargetPoleLocation != PoleLocation.None) {
            if (liftSpeed != 0) {
                resetState();
            }
        }

        this.liftSpeed = liftSpeed;

        if (elbowOpen) {
            robotState.ElbowServoPosition = ElbowServoPosition.Out;
        } else if (elbowClosed) {
            robotState.ElbowServoPosition = ElbowServoPosition.In;
        }

        if (latchOpen) {
            robotState.ClawServoPosition = ClawServoPosition.Open;
        } else if (latchClosed) {
            robotState.ClawServoPosition = ClawServoPosition.Closed;
        }

        if (intake) {
            robotState.LiftFilpServoPosition = LiftFilpServoPosition.Pickup;
        } else if (dropOff) {
            robotState.LiftFilpServoPosition = LiftFilpServoPosition.DropOff;
        }

        if (lowPole) {
            resetState();
            robotState.TargetPoleLocation = PoleLocation.Low;
        } else if (mediumPole) {
            resetState();
            robotState.TargetPoleLocation = PoleLocation.Medium;
        } else if (highPole) {
            resetState();
            robotState.TargetPoleLocation = PoleLocation.High;
        } else if (returnPosition) {
            resetState();
            robotState.TargetPoleLocation = PoleLocation.Return;
        }
    }

    private void resetState() {
        runningToPosition = false;
        atPosition = false;
        armReady = false;
        atZeroPosition = false;
        robotState.TargetPoleLocation = PoleLocation.None;
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(0);
    }
}
