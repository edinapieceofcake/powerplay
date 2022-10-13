package edu.edina.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TestIntake extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        CRServo intakeServo = hardwareMap.get(CRServo.class, "intakeServo");

        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()){
            if (gamepad1.dpad_up){
                armServo.setPosition(armServo.getPosition()+0.1);
            }
            if (gamepad1.dpad_down) {
                armServo.setPosition(armServo.getPosition()-0.1);
            }
            if (gamepad1.left_trigger != 0) {
                intakeServo.setPower(gamepad1.left_trigger);
            } else {
                intakeServo.setPower(-gamepad1.right_trigger);
            }

            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Intake Motor", intakeMotor.getCurrentPosition());
            telemetry.addData("Arm Servo", armServo.getPosition());
            telemetry.addData("Intake Servo", intakeServo.getPower());
            telemetry.update();
        }
    }
}
