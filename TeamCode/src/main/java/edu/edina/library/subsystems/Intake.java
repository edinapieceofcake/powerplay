package edu.edina.library.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.SlideArmMotorAction;
import edu.edina.library.util.IntakeServoAction;
import edu.edina.library.util.RobotState;
import edu.edina.library.util.SlideMotorAction;

@Config
public class Intake extends Subsystem {
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
    private DcMotorEx intakeMotor;
    private Servo slideArmServo;
    private CRServo intakeServo;
    private RobotState robotState;
    private DigitalChannel armSwitch;
    private DigitalChannel slideSwitch;
    private DigitalChannel mainIntakeSwitch;

    private boolean armResetOnce;
    private boolean slideMotorReset;
    private boolean armMotorReset;

    private boolean foldingArmInRunning = false;
    private boolean atConeDrop = false;
    private boolean droppedOffCone = false;
    private long droppedOffTime = 0;

    public Intake(HardwareMap map, RobotState robotState){
        try {
            slideMotor = map.get(DcMotorEx.class, "slideMotor");
            intakeMotor = map.get(DcMotorEx.class, "intakeMotor");
            slideArmServo = map.get(Servo.class, "slideArmServo");
            intakeServo = map.get(CRServo.class, "intakeServo");
            armSwitch = map.get(DigitalChannel.class, "armSwitch");
            slideSwitch = map.get(DigitalChannel.class, "slideSwitch");
            mainIntakeSwitch = map.get(DigitalChannel.class, "mainIntakeSwitch");

            armSwitch.setMode(DigitalChannel.Mode.INPUT);
            slideSwitch.setMode(DigitalChannel.Mode.INPUT);
            mainIntakeSwitch.setMode(DigitalChannel.Mode.INPUT);

            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            robotState.IntakeSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.IntakeSuccessfullySetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (robotState.AutoFoldInArm) {
            // auto folding in the arm
            if (!foldingArmInRunning) {
                slideMotor.setTargetPosition(TRANSFERSLIDEPOSITION);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slideMotor.setPower(MOTORSPEED);
                intakeMotor.setTargetPosition(TRANSFERARMPOSITION);
                intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                intakeMotor.setPower(MOTORSPEED);
                slideArmServo.setPosition(ENDSERVOPOSITION);
                intakeServo.setPower(0.1);
                foldingArmInRunning = true;
                atConeDrop = false;
                droppedOffCone = false;
                droppedOffTime = 0;
            } else if (!atConeDrop) {
                long slidediff = Math.abs(slideMotor.getCurrentPosition() - TRANSFERSLIDEPOSITION);
                long intakediff = Math.abs(intakeMotor.getCurrentPosition() - TRANSFERARMPOSITION);
                robotState.IntakeDiff = intakediff;
                robotState.SlideDiff = slidediff;
                if ((intakediff < 50) && (slidediff < 50)) {
                    intakeServo.setPower(-0.5);
                    atConeDrop = true;
                    droppedOffTime = System.currentTimeMillis();
                    robotState.DroppedOfftime = droppedOffTime;
                }
            } else if (atConeDrop && !droppedOffCone && (System.currentTimeMillis() > (droppedOffTime + 1000))) {
                intakeServo.setPower(0);
                droppedOffCone = true;
                robotState.DroppedOffCone = droppedOffCone;
                slideMotor.setTargetPosition(MIDDLESLIDEPOSITION);
                intakeMotor.setTargetPosition(MIDDLEARMPOSITION);
            } else if (droppedOffCone) {
                long slidediff = Math.abs(slideMotor.getCurrentPosition() - MIDDLESLIDEPOSITION);
                long intakediff = Math.abs(intakeMotor.getCurrentPosition() - MIDDLEARMPOSITION);
                robotState.IntakeDiff = intakediff;
                robotState.SlideDiff = slidediff;
                if ((intakediff < 50) && (slidediff < 50)) {
                    // stop everything
                    intakeMotor.setPower(0);
                    slideMotor.setPower(0);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    robotState.AutoFoldInArm = false;
                    foldingArmInRunning = false;
                    droppedOffCone = false;
                    atConeDrop = false;
                    droppedOffTime = 0;
                }
            }
        } else {
            // manual control area
            if (robotState.SlideMotorAction == SlideMotorAction.SlideIn) {
                slideMotor.setPower(-.5);
            } else if (robotState.SlideMotorAction == SlideMotorAction.SlideOut) {
                slideMotor.setPower(.5);
            } else {
                slideMotor.setPower(0);
            }

            if (robotState.IntakeServoAction == IntakeServoAction.Intake) {
                if (!mainIntakeSwitch.getState()) {
                    intakeServo.setPower(0.1);
                } else {
                    intakeServo.setPower(0.5);
                }
            } else if (robotState.IntakeServoAction == IntakeServoAction.Expel) {
                intakeServo.setPower(-0.5);
            } else if (robotState.IntakeServoAction == IntakeServoAction.Idle) {
                intakeServo.setPower(0.0);
            }

            if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldIn) {
                intakeMotor.setPower(-.5);
            } else if (robotState.SlideArmMotorAction == SlideArmMotorAction.FoldOut) {
                intakeMotor.setPower(.5);
            } else {
                intakeMotor.setPower(0);
            }
        }

        if (armSwitch.getState()) {
            if (!armMotorReset) {
                armMotorReset = true;
                armResetOnce = true;
                intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            armMotorReset = false;
        }

        if (slideSwitch.getState()) {
            if (!slideMotorReset) {
                slideMotorReset = true;
                slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            slideMotorReset = false;
        }

        if (armResetOnce) {
            robotState.SlideArmServoLocation = -.7 / 1560 * intakeMotor.getCurrentPosition() + .8;
            if (intakeMotor.getCurrentPosition() > 1350) {
                robotState.SlideArmServoLocation = ENDSERVOPOSITION;
            }
            slideArmServo.setPosition(robotState.SlideArmServoLocation);
        }

        robotState.IntakeMotorLocation = intakeMotor.getCurrentPosition();
        robotState.SlideMotorLocation = slideMotor.getCurrentPosition();
        robotState.SlideIntakeSwitch = mainIntakeSwitch.getState();
        robotState.SlideSwitch = slideSwitch.getState();
        robotState.ArmSwitch = armSwitch.getState();
    }

    public void setIntakeProperties(boolean intakeCone, boolean expelCone, boolean slideIn, boolean slideOut,
                                    boolean foldArmOut, boolean foldArmIn, boolean autoFoldArmIn,
                                    boolean autoFoldArmOut) {

        if (slideIn) {
            robotState.SlideMotorAction = SlideMotorAction.SlideIn;
        } else if (slideOut) {
            robotState.SlideMotorAction = SlideMotorAction.SlideOut;
        } else {
            robotState.SlideMotorAction = SlideMotorAction.Idle;
        }

        if (intakeCone) {
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
        }

        if (foldArmIn) {
            robotState.SlideArmMotorAction = SlideArmMotorAction.FoldIn;
        } else if (foldArmOut) {
            robotState.SlideArmMotorAction = SlideArmMotorAction.FoldOut;
        } else {
            robotState.SlideArmMotorAction = SlideArmMotorAction.Idle;
        }

        if (armResetOnce && autoFoldArmIn) {
            robotState.AutoFoldInArm = true;
        }
    }
}
