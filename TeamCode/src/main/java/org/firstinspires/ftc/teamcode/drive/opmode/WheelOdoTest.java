package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class WheelOdoTest extends OpMode {
    private DcMotorEx leftFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightFront;
    private DcMotorEx rightRear;

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
    }

    @Override
    public void loop() {
        if (gamepad1.x) {
            leftFront.setPower(1);
        } else {
            leftFront.setPower(0);
        }

        if (gamepad1.y) {
            leftRear.setPower(1);
        } else {
            leftRear.setPower(0);
        }

        if (gamepad1.b) {
            rightFront.setPower(1);
        } else {
            rightFront.setPower(0);
        }

        if (gamepad1.a) {
            rightRear.setPower(1);
        } else {
            rightRear.setPower(0);
        }

        telemetry.addData("Left Front Encoder", leftFront.getCurrentPosition());
        telemetry.addData("Left Right Encoder", leftRear.getCurrentPosition());
        telemetry.addData("Right Front Encoder", rightFront.getCurrentPosition());
        telemetry.addData("Right Rear Encoder", rightRear.getCurrentPosition());
    }
}
