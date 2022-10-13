package edu.edina.opmodes.teleop;

import edu.edina.library.util.Stickygamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "BlueTeleop", group = "teleop")

public class TeleOp extends OpMode {
    private Robot robot;
    private Stickygamepad _gamepad1;
    private Stickygamepad _gamepad2;


    public void init() {
        _gamepad1 = new Stickygamepad(gamepad1);
        _gamepad2 = new Stickygamepad(gamepad2);
        robot = new Robot(this, telemetry);
        robot.start();
    }

    @Override
    public void start() {
    }

    public void loop() {

        _gamepad1.update();
        _gamepad2.update();


        robot.drive.setVelocity(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad2.right_stick_y
        );


        // set things into the robot from the gamepad or other sensors

        telemetry.update();
        robot.drive.setVelocity(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_y);
        robot.lift.SetSpeed(gamepad2.right_stick_y);

    }

    @Override
    public  void stop() {
        robot.stop();
    }
}
