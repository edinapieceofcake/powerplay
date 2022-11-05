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
        Servo liftFlipServo = hardwareMap.get(Servo.class, "liftFlipServo");
        Servo elbowServo = hardwareMap.get(Servo.class, "elbowServo");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        elbowServo.setPosition(0);
        clawServo.setPosition(0);
        waitForStart();

        while(opModeIsActive()){
            pad1.update();

            if (pad1.dpad_left){
                elbowServo.setPosition(elbowServo.getPosition()+0.05);
            }
            if (pad1.dpad_right){
                elbowServo.setPosition(elbowServo.getPosition()-0.05);
            }

            if (pad1.right_bumper){
                clawServo.setPosition(clawServo.getPosition()+0.05);
            }

            if (pad1.left_bumper){
                clawServo.setPosition(clawServo.getPosition()-0.05);
            }

            if (pad1.dpad_up){
                liftFlipServo.setPosition(liftFlipServo.getPosition()+0.05);
            }
            if (pad1.dpad_down){
                liftFlipServo.setPosition(liftFlipServo.getPosition()-0.05);
            }

            if (gamepad1.right_trigger != 0) {
                liftMotor.setPower(1);
            } else if (gamepad1.left_trigger != 0) {
                liftMotor.setPower(-1);
            } else {
                liftMotor.setPower(0);
            }

            telemetry.addData("Motor Position", liftMotor.getCurrentPosition());
            telemetry.addData("Elbow Position", elbowServo.getPosition());
            telemetry.addData("Claw Position", clawServo.getPosition());
            telemetry.addData("Flip Position", liftFlipServo.getPosition());
            telemetry.update();
        }
    }
}
