package edu.edina.opmodes.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.Stickygamepad;

@TeleOp
@Config
public class TestIntake extends LinearOpMode {
    public static int LIFTRUNTOPOSITION = 1450;
    public static int SLIDERUNTOPOSITION = 700;
    public static double STARTSERVOPOSITION = 0.2;
    public static double ENDSERVOPOSITION = 0.15;
    public static double MOTORSPEED = 0.4;
    public static int TRANSFERARMPOSITION = 100;
    public static int TRANSFERSLIDEPOSITION = 500;

    @Override
    public void runOpMode() throws InterruptedException {
        Stickygamepad pad1 = new Stickygamepad(gamepad1);
        DcMotorEx slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        CRServo intakeServo = hardwareMap.get(CRServo.class, "intakeServo");
        DigitalChannel armSwitch = hardwareMap.get(DigitalChannel.class, "armSwitch");
        DigitalChannel slideSwitch = hardwareMap.get(DigitalChannel.class, "slideSwitch");
        DigitalChannel mainintakeSwitch = hardwareMap.get(DigitalChannel.class, "mainintakeSwitch");
        boolean armMotorReset = false;
        boolean slideMotorReset = false;
        boolean resetOnce = false;
        boolean runningArmToPosition = false;
        boolean runningSlideToPosition = false;
        boolean pickingup = false;
        boolean mainintakeloaded = false;
        boolean iamhome = true;
        double servoLocation = 0.0;

        armSwitch.setMode(DigitalChannel.Mode.INPUT);
        mainintakeSwitch.setMode(DigitalChannel.Mode.INPUT);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pickingup = false;


        // range from .8 to .1, 0 - 900
        armServo.setPosition(0.8);
        waitForStart();


        while (opModeIsActive()){
            if (resetOnce) {
                servoLocation = -.7/1560 * intakeMotor.getCurrentPosition() + .8;
                if (intakeMotor.getCurrentPosition() > 1350) {
                    servoLocation = ENDSERVOPOSITION;
                }
                armServo.setPosition(servoLocation);
            }
            pad1.update();
            if (pad1.dpad_up){
                armServo.setPosition(armServo.getPosition()+0.1);
                resetOnce = false;
            }
            if (pad1.dpad_down) {
                armServo.setPosition(armServo.getPosition()-0.1);
                ;
            }

            if (pad1.a) {
                if (!runningArmToPosition) {
                    intakeMotor.setTargetPosition(LIFTRUNTOPOSITION);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    intakeMotor.setPower(MOTORSPEED);
                }
                runningArmToPosition = true;
            }

            if (pad1.b) {
                if (!runningArmToPosition) {
                    intakeMotor.setTargetPosition(0);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    intakeMotor.setPower(MOTORSPEED);
                }
                runningArmToPosition = true;
            }

            if (pad1.x) {
                pickingup = true;
                mainintakeloaded = false;
                iamhome = false;
                slideMotor.setTargetPosition(SLIDERUNTOPOSITION);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slideMotor.setPower(MOTORSPEED);
                runningArmToPosition=true;
                intakeMotor.setTargetPosition(LIFTRUNTOPOSITION);
                intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                intakeMotor.setPower(MOTORSPEED);
                armServo.setPosition(STARTSERVOPOSITION);
                intakeServo.setPower(0.5);
            }

            if (pad1.y) {
                pickingup = false;
                slideMotor.setTargetPosition(TRANSFERSLIDEPOSITION);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slideMotor.setPower(MOTORSPEED);
                runningArmToPosition=true;
                intakeMotor.setTargetPosition(TRANSFERARMPOSITION);
                intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                intakeMotor.setPower(MOTORSPEED);
                armServo.setPosition(ENDSERVOPOSITION);
                intakeServo.setPower(0.1);
            }

            if (!mainintakeSwitch.getState()) {
                    if (pickingup = true) {
                        if (!mainintakeloaded) {
                            intakeServo.setPower(0.1);
                            mainintakeloaded = true;
                            pickingup = false;
                        }
                    }
            }

            if ((slideMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) &&
                (intakeMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) && !pickingup) {
                double slidediff = Math.abs(slideMotor.getCurrentPosition() - slideMotor.getTargetPosition()) / Math.abs(slideMotor.getTargetPosition());
                double intakediff = Math.abs(intakeMotor.getCurrentPosition() - intakeMotor.getTargetPosition()) / Math.abs(intakeMotor.getTargetPosition());
                if ((intakediff < .05) && (slidediff < 0.05))
                {
                    intakeServo.setPower(-0.5);
                    iamhome = true;
                }
            }

            if (gamepad1.left_trigger != 0) {
                intakeServo.setPower(gamepad1.left_trigger);
            } else if (gamepad1.right_trigger != 0){
                intakeServo.setPower(-gamepad1.right_trigger);
            } else {
               // intakeServo.setPower(0);
            }

            if (gamepad1.left_bumper) {
                if (runningArmToPosition) {
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                intakeMotor.setPower(MOTORSPEED);
                runningArmToPosition = false;
            } else if (gamepad1.right_bumper) {
                if (runningArmToPosition) {
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                intakeMotor.setPower(-MOTORSPEED);
                runningArmToPosition = false;
            } else if (!runningArmToPosition){
                intakeMotor.setPower(0);
            }

            if (armSwitch.getState()) {
                if (!armMotorReset) {
                    armMotorReset = true;
                    resetOnce = true;
                    intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            } else {
                armMotorReset = false;
            }

            if (slideSwitch.getState())
            {
                if (!slideMotorReset) {
                    slideMotorReset = true;
                    slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            } else {
                slideMotorReset = false;
            }

            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Intake Motor", intakeMotor.getCurrentPosition());
            telemetry.addData("Intake Zero", intakeMotor.getZeroPowerBehavior());
            telemetry.addData("Arm Servo", armServo.getPosition());
            telemetry.addData("Arm Servo Calculated", servoLocation);
            telemetry.addData("Intake Servo", intakeServo.getPower());
            telemetry.addData("Arm Switch", armSwitch.getState());
            telemetry.addData("Main Intake Switch", mainintakeSwitch.getState());
            telemetry.addData("Intake Servo Power", intakeServo.getPower());
            telemetry.addData("Iamhome", iamhome);
 //           telemetry.addData("Main Intake loaded", mainintakeloaded.getState());
            telemetry.addData("Arm Motor Reset", armMotorReset);
            telemetry.update();
        }
    }
}
