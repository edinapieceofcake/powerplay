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
    public static int STARTARMPOSITION = 1450;
    public static int STARTSLIDEPOSITION = 700;
    public static double STARTSERVOPOSITION = 0.2;
    public static double ENDSERVOPOSITION = 0.19;
    public static double MOTORSPEED = 0.4;
    public static int TRANSFERSLIDEPOSITION = 530;
    public static int TRANSFERARMPOSITION = 150;
    public static int MIDDLESLIDEPOSITION = 930;
    public static int MIDDLEARMPOSITION = 410;

    private DcMotorEx slideMotor;
    private Servo clampServo;
    private Servo armFlipServo;
    private RobotState robotState;
    private long lastUpdate;
    private boolean flipUpdated;

    private boolean armResetOnce;
    private boolean slideMotorReset;
    private boolean armMotorReset;

    private boolean foldingArmInRunning = false;
    private boolean atConeDrop = false;
    private boolean droppedOffCone = false;
    private long droppedOffTime = 0;

    private int originalArmPosition;
    private int originalSlidePosition;

    private boolean armRanInOnce = false;

    private boolean foldingArmOutRunning = false;
    private boolean atConePickup = false;

    public Intake2(HardwareMap map, RobotState robotState){
        try {
            slideMotor = map.get(DcMotorEx.class, "slideMotor");
            clampServo = map.get(Servo.class, "clampServo");
            armFlipServo = map.get(Servo.class, "armFlipServo");

            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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
                slideMotor.setTargetPosition(580);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slideMotor.setPower(1);
                foldingArmInRunning = true;
                atConeDrop = false;
                robotState.FlipPosition = .25;
                armFlipServo.setPosition(robotState.FlipPosition);
            } else if (!atConeDrop) {
                int diff = Math.abs(Math.abs(slideMotor.getCurrentPosition()) - 580);
                if (diff < 10) {
                    atConeDrop = true;
                    clampServo.setPosition(1);
                    robotState.IntakeClampOpen = true;
                    droppedOffTime = System.currentTimeMillis();
                    droppedOffCone = false;
                }
            } else if (!droppedOffCone) {
                if (System.currentTimeMillis() > (droppedOffTime + 2000)) {
                    robotState.FlipPosition = .45;
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

        if (System.currentTimeMillis() > (lastUpdate + 25) && (flipUp || flipDown)) {
            if (flipUp) {
                robotState.FlipPosition += .05;
            } else if (flipDown) {
                robotState.FlipPosition -= .05;
            }

            robotState.FlipPosition = Math.min(.80, Math.max(.3, robotState.FlipPosition));
            lastUpdate = System.currentTimeMillis();
        }

        if (autoFoldArmIn) {
            robotState.AutoFoldInArm = true;
        }
    }
}
