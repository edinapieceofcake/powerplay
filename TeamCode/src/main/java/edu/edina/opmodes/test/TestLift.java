package edu.edina.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp()
public class TestLift extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotor");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        Servo latchServo = hardwareMap.get(Servo.class, "latchServo");

        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while(opModeIsActive()){
            if (gamepad1.dpad_up){
                armServo.setPosition(armServo.getPosition()+0.1);
            }
            if (gamepad1.dpad_down){
                armServo.setPosition(armServo.getPosition()-0.1);
            }
            if (gamepad1.right_bumper){
                latchServo.setPosition(latchServo.getPosition()+0.1);
            }
            if (gamepad1.left_bumper){
                latchServo.setPosition(latchServo.getPosition()-0.1);
            }

            telemetry.addData("Motor Position", liftMotor.getCurrentPosition());
            telemetry.addData("Arm Position",armServo.getPosition());
            telemetry.addData("Latch Position",latchServo.getPosition());
            telemetry.update();
        }
    }
}
