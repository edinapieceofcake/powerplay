package edu.edina.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.Stickygamepad;

@TeleOp
public class TestIntakeAndLift extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Stickygamepad pad1 = new Stickygamepad(gamepad1);
        Stickygamepad pad2 = new Stickygamepad(gamepad2);
        DcMotorEx slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        DcMotorEx liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotor");
        CRServo intakeServo = hardwareMap.get(CRServo.class, "intakeServo");
        DigitalChannel armSwitch = hardwareMap.get(DigitalChannel.class, "armSwitch");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        Servo liftArmServo = hardwareMap.get(Servo.class, "liftArmServo");
        Servo latchServo = hardwareMap.get(Servo.class, "latchServo");
        boolean armMotorReset = false;
        boolean resetOnce = false;
        double servoLocation = 0.0;

        armSwitch.setMode(DigitalChannel.Mode.INPUT);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // range from .8 to .1, 0 - 900
        armServo.setPosition(0.8);

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armServo.setPosition(0);
        latchServo.setPosition(0);
        waitForStart();

        while (opModeIsActive()) {
            if (resetOnce) {
                servoLocation = -.000777777777777777 * intakeMotor.getCurrentPosition() + .8;
                armServo.setPosition(servoLocation);
            }
            pad1.update();
            pad2.update();
            if (pad1.dpad_up) {
                armServo.setPosition(armServo.getPosition() + 0.1);
                resetOnce = false;
            }
            if (pad1.dpad_down) {
                armServo.setPosition(armServo.getPosition() - 0.1);
                resetOnce = false;
            }
            if (gamepad1.left_trigger != 0) {
                intakeServo.setPower(gamepad1.left_trigger);
            } else if (gamepad1.right_trigger != 0) {
                intakeServo.setPower(-gamepad1.right_trigger);
            } else {
                intakeServo.setPower(0);
            }

            if (gamepad1.left_bumper) {
                intakeMotor.setPower(.5);
            } else if (gamepad1.right_bumper) {
                intakeMotor.setPower(-.5);
            } else {
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

            // lift
            if (pad2.dpad_up) {
                liftArmServo.setPosition(liftArmServo.getPosition() + 0.1);
            }
            if (pad2.dpad_down) {
                liftArmServo.setPosition(liftArmServo.getPosition() - 0.1);
            }

            if (pad2.right_bumper) {
                latchServo.setPosition(latchServo.getPosition() + 0.1);
            }

            if (pad2.left_bumper) {
                latchServo.setPosition(latchServo.getPosition() - 0.1);
            }

            if (gamepad2.right_trigger != 0) {
                liftMotor.setPower(1);
            } else if (gamepad2.left_trigger != 0) {
                liftMotor.setPower(-1);
            } else {
                liftMotor.setPower(0);
            }

            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Intake Motor", intakeMotor.getCurrentPosition());
            telemetry.addData("Intake Zero", intakeMotor.getZeroPowerBehavior());
            telemetry.addData("Arm Servo", armServo.getPosition());
            telemetry.addData("Arm Servo Calculated", servoLocation);
            telemetry.addData("Intake Servo", intakeServo.getPower());
            telemetry.addData("Arm Switch", armSwitch.getState());
            telemetry.addData("Arm Motor Reset", armMotorReset);
            telemetry.addData("Motor Position", liftMotor.getCurrentPosition());
            telemetry.addData("Arm Position", liftArmServo.getPosition());
            telemetry.addData("Latch Position", latchServo.getPosition());
            telemetry.update();
        }
    }
}
