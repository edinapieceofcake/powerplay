package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.apache.commons.math3.util.Precision;
import org.opencv.core.Mat;

import java.lang.ref.PhantomReference;

import edu.edina.library.util.ClawServoPosition;
import edu.edina.library.util.ElbowServoPosition;
import edu.edina.library.util.LiftFilpServoPosition;
import edu.edina.library.util.PoleLocation;
import edu.edina.library.util.RobotState;

public class Lift2 extends Subsystem {

    private static double CLAWOPENPOSITION = 0.47;
    private static double CLAWCLOSEDPOSITION = 0.55;

    private static double ELBOWINPOSITION = 0.73;
    private static int ELBOWINPOSITION100 = 73;
    private static double ELBOWOUTPOSITION = .6;
    private static int ELBOWOUTPOSITION100 = 60;

    private static double LIFTPICKUPPOSITION = .15;
    private static double LIFTPICKUPPOSITION100 = 15;
    private static double LIFTDROPOFFPOSITION = .9;
    private static double LIFTMIDDLEPOSITION = .6;

    private static int POLEPOSITIONLOW = -1008;
    private static int POLEPOSITIONMIDDLE = -1900;
    private static int POLEPOSITIONHIGH = -2600;

    private static int ARMOUTPOSITION = 200;
    private static int LIFTRETURNHEiGHT = -180;

    private static int ARMFLIPWAITTIME = 750;
    private static int ELBOWINWAITTIME = 750;

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

            clawServo.setPosition(CLAWCLOSEDPOSITION);
            robotState.ClawServoPosition = ClawServoPosition.Closed;

            elbowServo.setPosition(ELBOWOUTPOSITION);
            robotState.ElbowServoPosition = ElbowServoPosition.Out;

            liftFlipServo.setPosition(LIFTMIDDLEPOSITION);
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
                    clawServo.setPosition(CLAWOPENPOSITION);
                    robotState.ClawServoPosition = ClawServoPosition.Open;
                    liftFlipServo.setPosition(LIFTPICKUPPOSITION);
                    robotState.LiftFilpServoPosition = LiftFilpServoPosition.Pickup;
                    runningToPosition = true;
                    atPosition = false;
                    returnStartedTime = System.currentTimeMillis();
                } else if (!atPosition) {
                    if ((System.currentTimeMillis() > (returnStartedTime + ARMFLIPWAITTIME)) && (Math.round(liftFlipServo.getPosition() * 100) == LIFTPICKUPPOSITION100)) {
                        atPosition = true;
                        elbowServo.setPosition(ELBOWINPOSITION);
                        robotState.ElbowServoPosition = ElbowServoPosition.In;
                        armReady = false;
                        returnStartedTime = System.currentTimeMillis();
                    }
                } else if (!armReady) {
                    if ((System.currentTimeMillis() > (returnStartedTime + ELBOWINWAITTIME)) && (Math.round(elbowServo.getPosition() * 100) == ELBOWINPOSITION100)) {
                        liftMotor.setTargetPosition(LIFTRETURNHEiGHT);
                        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        liftMotor.setPower(1);
                        atZeroPosition = false;
                        armReady = true;
                    }
                } else if (!atZeroPosition) {
                    robotState.LiftDiff = Math.abs(liftMotor.getCurrentPosition());

                    if (robotState.LiftDiff < Math.abs(LIFTRETURNHEiGHT)) {
                        resetState();
                    }
                }
            } else {
                if (!runningToPosition) {
                    if (robotState.TargetPoleLocation == PoleLocation.Low) {
                        targetPosition = POLEPOSITIONLOW;
                    } else if (robotState.TargetPoleLocation == PoleLocation.Medium) {
                        targetPosition = POLEPOSITIONMIDDLE;
                    } else if (robotState.TargetPoleLocation == PoleLocation.High) {
                        targetPosition = POLEPOSITIONHIGH;
                    }

                    clawServo.setPosition(CLAWCLOSEDPOSITION);
                    robotState.ClawServoPosition = ClawServoPosition.Closed;
                    liftMotor.setTargetPosition(targetPosition);
                    liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    liftMotor.setPower(1);
                    runningToPosition = true;
                    atPosition = false;
                } else if (!atPosition) {
                    robotState.LiftDiff = Math.abs(Math.abs(liftMotor.getCurrentPosition()) - Math.abs(targetPosition));

                    if ((Math.abs(liftMotor.getCurrentPosition()) > ARMOUTPOSITION) && (Math.round(elbowServo.getPosition() * 100) != ELBOWOUTPOSITION100)) {
                        elbowServo.setPosition(ELBOWOUTPOSITION);
                        robotState.ElbowServoPosition = ElbowServoPosition.Out;
                    }

                    if (robotState.LiftDiff < 10) {
                        atPosition = true;
                        armReady = false;
                    }
                } else if (!armReady) {
                    liftFlipServo.setPosition(LIFTDROPOFFPOSITION);
                    robotState.LiftFilpServoPosition = LiftFilpServoPosition.DropOff;
                    armReady = true;
                } else {
                    if (robotState.ClawServoPosition == ClawServoPosition.Open) {
                        clawServo.setPosition(CLAWOPENPOSITION);
                    } else if (robotState.ClawServoPosition == ClawServoPosition.Closed) {
                        clawServo.setPosition(CLAWCLOSEDPOSITION);
                    }
                }
            }
        } else {
            if (robotState.ClawServoPosition == ClawServoPosition.Open) {
                clawServo.setPosition(CLAWOPENPOSITION);
            } else if (robotState.ClawServoPosition == ClawServoPosition.Closed) {
                clawServo.setPosition(CLAWCLOSEDPOSITION);
            }

            if (robotState.ElbowServoPosition == ElbowServoPosition.In) {
                elbowServo.setPosition(ELBOWINPOSITION);
            } else if (robotState.ElbowServoPosition == ElbowServoPosition.Out) {
                elbowServo.setPosition(ELBOWOUTPOSITION);
            }

            if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.Pickup) {
                liftFlipServo.setPosition(LIFTPICKUPPOSITION);
            } else if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.DropOff) {
                liftFlipServo.setPosition(LIFTDROPOFFPOSITION);
            } else if (robotState.LiftFilpServoPosition == LiftFilpServoPosition.Middle) {
                liftFlipServo.setPosition(LIFTMIDDLEPOSITION);
            }

            liftMotor.setPower(liftSpeed);
        }

        robotState.LiftMotorLocation = liftMotor.getCurrentPosition();
        robotState.ClawPosition = Math.round(clawServo.getPosition() * 100);
        robotState.ElbowPosition = Math.round(elbowServo.getPosition() * 100);
        robotState.LiftFlipPosition = Math.round(liftFlipServo.getPosition() * 100);
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
