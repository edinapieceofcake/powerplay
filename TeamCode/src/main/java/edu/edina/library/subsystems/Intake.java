package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.SlideArmMotorAction;
import edu.edina.library.util.IntakeServoAction;
import edu.edina.library.util.RobotState;
import edu.edina.library.util.SlideMotorAction;

public class Intake extends Subsystem {
    private DcMotorEx slideMotor;
    private DcMotorEx slideArmMotor;
    private Servo slideArmServo;
    private CRServo intakeServo;
    private RobotState robotState;

    public Intake(HardwareMap map, RobotState robotState){
        try {
            slideMotor = map.get(DcMotorEx.class, "slideMotor");
            slideArmMotor = map.get(DcMotorEx.class, "slideArmMotor");
            slideArmServo = map.get(Servo.class, "slideArmServo");
            intakeServo = map.get(CRServo.class, "intakeServo");

            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robotState.IntakeSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.IntakeSuccessfullySetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.IntakeSuccessfullySetup) {
            if (robotState.SlideMotorAction == SlideMotorAction.SlideIn) {
                slideMotor.setPower(.5);
            } else if (robotState.SlideMotorAction == SlideMotorAction.SlideOut) {
                slideMotor.setPower(-.5);
            } else {
                slideMotor.setPower(0);
            }

            if (robotState.IntakeServoAction == IntakeServoAction.Intake) {
                intakeServo.setPower(.5);
            } else if (robotState.IntakeServoAction == IntakeServoAction.Expel) {
                intakeServo.setPower(-.5);
            } else {
                intakeServo.setPower(0);
            }

            if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldIn) {
                slideArmMotor.setPower(.5);
            } else if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldOut) {
                slideArmMotor.setPower(-.5);
            } else {
                slideArmMotor.setPower(0);
            }

            robotState.SlideArmMotorLocation = slideArmMotor.getCurrentPosition();
            robotState.SlideMotorLocation = slideMotor.getCurrentPosition();
            robotState.SlideArmServoLocation = slideArmServo.getPosition();
        }
    }

    public void setIntakeProperties(boolean intakeCone, boolean expelCone, boolean slideIn, boolean slideOut,
                                    boolean foldArmOut, boolean foldArmIn) {
        if (intakeCone && expelCone) {
            robotState.IntakeServoAction = IntakeServoAction.Idle;
        } else if (intakeCone) {
            if (robotState.IntakeServoAction == IntakeServoAction.Intake) {
                robotState.IntakeServoAction = IntakeServoAction.Idle;
            } else {
                robotState.IntakeServoAction = IntakeServoAction.Intake;
            }
        } else if (expelCone) {
            if (robotState.IntakeServoAction == IntakeServoAction.Expel) {
                robotState.IntakeServoAction = IntakeServoAction.Idle;
            } else {
                robotState.IntakeServoAction = IntakeServoAction.Expel;
            }
        } else {
            robotState.IntakeServoAction = IntakeServoAction.Idle;
        }

        if (slideIn && slideOut) {
            robotState.SlideMotorAction = SlideMotorAction.Idle;
        } else if (slideIn) {
            robotState.SlideMotorAction = SlideMotorAction.SlideIn;
        } else if (slideOut) {
            robotState.SlideMotorAction = SlideMotorAction.SlideOut;
        } else {
            robotState.SlideMotorAction = SlideMotorAction.Idle;
        }

        if (foldArmIn && foldArmOut) {
            robotState.SlideArmMotorAction = SlideArmMotorAction.Idle;
        } else if (foldArmIn) {
            if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldIn) {
                robotState.SlideArmMotorAction = SlideArmMotorAction.Idle;
            } else {
                robotState.SlideArmMotorAction = SlideArmMotorAction.FoldIn;
            }
        } else if (foldArmOut) {
            if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldOut) {
                robotState.SlideArmMotorAction = SlideArmMotorAction.Idle;
            } else {
                robotState.SlideArmMotorAction = SlideArmMotorAction.FoldOut;
            }
        } else {
            robotState.SlideArmMotorAction = SlideArmMotorAction.Idle;
        }
    }
}
