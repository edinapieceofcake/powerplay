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
    public static int RUNTOPOSITIONLOCATION = 1300;
    public static double ENDSERVOPOSITION = 0.2;

    @Override
    public void runOpMode() throws InterruptedException {
        Stickygamepad pad1 = new Stickygamepad(gamepad1);
//        DcMotorEx slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        CRServo intakeServo = hardwareMap.get(CRServo.class, "intakeServo");
        DigitalChannel armSwitch = hardwareMap.get(DigitalChannel.class, "armSwitch");
        boolean armMotorReset = false;
        boolean resetOnce = false;
        boolean runningToPosition = false;
        double servoLocation = 0.0;

        armSwitch.setMode(DigitalChannel.Mode.INPUT);
        //slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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
                resetOnce = false;
            }

            if (pad1.a) {
                if (!runningToPosition) {
                    intakeMotor.setTargetPosition(RUNTOPOSITIONLOCATION);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    intakeMotor.setPower(.5);
                }
                runningToPosition = true;
            }

            if (pad1.b) {
                if (!runningToPosition) {
                    intakeMotor.setTargetPosition(0);
                    intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    intakeMotor.setPower(.5);
                }
                runningToPosition = true;
            }

            if (gamepad1.left_trigger != 0) {
                intakeServo.setPower(gamepad1.left_trigger);
            } else if (gamepad1.right_trigger != 0){
                intakeServo.setPower(-gamepad1.right_trigger);
            } else {
                intakeServo.setPower(0);
            }

            if (gamepad1.left_bumper) {
                if (runningToPosition) {
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                intakeMotor.setPower(.5);
                runningToPosition = false;
            } else if (gamepad1.right_bumper) {
                if (runningToPosition) {
                    intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                intakeMotor.setPower(-.5);
                runningToPosition = false;
            } else if (!runningToPosition){
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

//            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Intake Motor", intakeMotor.getCurrentPosition());
            telemetry.addData("Intake Zero", intakeMotor.getZeroPowerBehavior());
            telemetry.addData("Arm Servo", armServo.getPosition());
            telemetry.addData("Arm Servo Calculated", servoLocation);
            telemetry.addData("Intake Servo", intakeServo.getPower());
            telemetry.addData("Arm Switch", armSwitch.getState());
            telemetry.addData("Arm Motor Reset", armMotorReset);
            telemetry.update();
        }
    }
}
