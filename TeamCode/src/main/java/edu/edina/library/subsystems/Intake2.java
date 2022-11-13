package edu.edina.library.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.IntakeServoAction;
import edu.edina.library.util.RobotState;
import edu.edina.library.util.SlideArmMotorAction;
import edu.edina.library.util.SlideMotorAction;

@Config
public class Intake2 extends Subsystem {
    public static int TRANSFERSLIDEPOSITION = 580;
    public static int MIDDLESLIDEPOSITION = 600;

    public static double MAXFLIPPOSITION = .8;
    public static double MINFLIPPOSITION = .3;
    public static double TRANSFERPOSITION = .25;
    public static double MIDDLEPOSITION = .45;
    public static double INCREMENTFLIP = .05;
    public static int INCREMENTTIMEOUT = 25;

    public static int DROPOFFTIMEOUT = 1000;

    private DcMotorEx slideMotor;
    private Servo clampServo;
    private Servo armFlipServo;
    private RobotState robotState;
    private long lastUpdate;

    private boolean foldingArmInRunning = false;
    private boolean atConeDrop = false;
    private boolean droppedOffCone = false;
    private boolean slidOut = false;
    private long droppedOffTime = 0;

    public Intake2(HardwareMap map, RobotState robotState){
        try {
            slideMotor = map.get(DcMotorEx.class, "slideMotor");
            clampServo = map.get(Servo.class, "clampServo");
            armFlipServo = map.get(Servo.class, "armFlipServo");

            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            robotState.FlipPosition = MIDDLEPOSITION;
            armFlipServo.setPosition(robotState.FlipPosition);

            robotState.IntakeClampOpen = false;
            clampServo.setPosition(1);

            robotState.IntakeSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.IntakeSuccessfullySetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.AutoFoldInArm) {
            if (!foldingArmInRunning) {
                slideMotor.setTargetPosition(TRANSFERSLIDEPOSITION);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slideMotor.setPower(1);
                foldingArmInRunning = true;
                atConeDrop = false;
                robotState.FlipPosition = TRANSFERPOSITION;
                armFlipServo.setPosition(robotState.FlipPosition);
            } else if (!atConeDrop) {
                int diff = Math.abs(Math.abs(slideMotor.getCurrentPosition()) - TRANSFERSLIDEPOSITION);
                if (diff < 10) {
                    atConeDrop = true;
                    clampServo.setPosition(1);
                    robotState.IntakeClampOpen = false;
                    droppedOffTime = System.currentTimeMillis();
                    droppedOffCone = false;
                }
            } else if (!droppedOffCone) {
                if (System.currentTimeMillis() > (droppedOffTime + DROPOFFTIMEOUT)) {
                    slideMotor.setTargetPosition(MIDDLESLIDEPOSITION);
                    slidOut = false;
                    droppedOffCone = true;
                }
            } else if (!slidOut) {
                int diff = Math.abs(Math.abs(slideMotor.getCurrentPosition()) - MIDDLESLIDEPOSITION);
                if (diff < 10) {
                    robotState.FlipPosition = MIDDLEPOSITION;
                    armFlipServo.setPosition(robotState.FlipPosition);
                    slideMotor.setPower(0);
                    slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    robotState.AutoFoldInArm = false;
                    foldingArmInRunning = false;
                }
            }
        } else {
            // manual control area
            if (robotState.SlideMotorAction == SlideMotorAction.SlideIn) {
                slideMotor.setPower(-1);
            } else if (robotState.SlideMotorAction == SlideMotorAction.SlideOut) {
                slideMotor.setPower(1);
            } else {
                slideMotor.setPower(0);
            }

            if (robotState.IntakeClampOpen) {
                clampServo.setPosition(0);
            } else {
                clampServo.setPosition(1);
            }

            armFlipServo.setPosition(robotState.FlipPosition);
        }

        robotState.SlideMotorLocation = slideMotor.getCurrentPosition();
    }

    public void setIntakeProperties(boolean toggleClamp, boolean slideIn, boolean slideOut,
                                    boolean flipUp, boolean flipDown, boolean autoFoldArmIn,
                                    boolean autoFoldArmOut) {

        if (slideIn) {
            robotState.SlideMotorAction = SlideMotorAction.SlideIn;
        } else if (slideOut) {
            robotState.SlideMotorAction = SlideMotorAction.SlideOut;
        } else {
            robotState.SlideMotorAction = SlideMotorAction.Idle;
        }

        if (toggleClamp) {
            robotState.IntakeClampOpen = !robotState.IntakeClampOpen;
        }

        if (System.currentTimeMillis() > (lastUpdate + INCREMENTTIMEOUT) && (flipUp || flipDown)) {
            if (flipUp) {
                robotState.FlipPosition += INCREMENTFLIP;
            } else if (flipDown) {
                robotState.FlipPosition -= INCREMENTFLIP;
            }

            robotState.FlipPosition = Math.min(MAXFLIPPOSITION, Math.max(MINFLIPPOSITION, robotState.FlipPosition));
            lastUpdate = System.currentTimeMillis();
        }

        if (autoFoldArmIn) {
            robotState.AutoFoldInArm = true;
        }
    }
}
