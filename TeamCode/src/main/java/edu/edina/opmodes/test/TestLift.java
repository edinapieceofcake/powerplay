package edu.edina.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import edu.edina.library.util.Stickygamepad;

@TeleOp()
public class TestLift extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Stickygamepad pad1 = new Stickygamepad(gamepad1);
        DcMotorEx liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotor");
        Servo armServo = hardwareMap.get(Servo.class, "armServo");
        Servo latchServo = hardwareMap.get(Servo.class, "latchServo");

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armServo.setPosition(0);
        latchServo.setPosition(0);
        waitForStart();

        while(opModeIsActive()){
            pad1.update();
            if (pad1.dpad_up){
                armServo.setPosition(armServo.getPosition()+0.1);
            }
            if (pad1.dpad_down){
                armServo.setPosition(armServo.getPosition()-0.1);
            }

            if (pad1.right_bumper){
                latchServo.setPosition(latchServo.getPosition()+0.1);
            }

            if (pad1.left_bumper){
                latchServo.setPosition(latchServo.getPosition()-0.1);
            }

            if (gamepad1.right_trigger != 0) {
                liftMotor.setPower(1);
            } else if (gamepad1.left_trigger != 0) {
                liftMotor.setPower(-1);
            } else {
                liftMotor.setPower(0);
            }

            telemetry.addData("Motor Position", liftMotor.getCurrentPosition());
            telemetry.addData("Arm Position",armServo.getPosition());
            telemetry.addData("Latch Position",latchServo.getPosition());
            telemetry.update();
        }
    }
}
