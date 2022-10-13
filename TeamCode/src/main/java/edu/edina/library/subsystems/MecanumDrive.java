package edu.edina.library.subsystems;


import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.RobotState;

public class MecanumDrive extends Subsystem{

    private double leftStickX;
    private double leftStickY;
    private double rightstickY;
    private RobotState robotState;
    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;
    private boolean successfulSetup;

    private com.arcrobotics.ftclib.drivebase.MecanumDrive drive;

    public MecanumDrive(HardwareMap map, RobotState robotState){
        try {
            frontLeft = new Motor(map, "frontLeft", Motor.GoBILDA.RPM_312);
            frontRight = new Motor(map, "frontRight", Motor.GoBILDA.RPM_312);
            backLeft = new Motor(map, "backLeft", Motor.GoBILDA.RPM_312);
            backRight = new Motor(map, "backRight", Motor.GoBILDA.RPM_312);

            drive = new com.arcrobotics.ftclib.drivebase.MecanumDrive(frontLeft, frontRight, backLeft, backRight);
            successfulSetup = true;
        } catch (Exception ex) {
            successfulSetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (successfulSetup) {
            drive.driveRobotCentric(leftStickX, leftStickY, rightstickY);
        }
    }

    public void setVelocity(double leftStickX, double leftStickY, double rightStickY) {
        this.leftStickX = leftStickX;
        this.leftStickY = leftStickY;
        this.rightstickY = rightStickY;
    }

    @Override
    public void telemetry(Telemetry telemetry) {
        if (successfulSetup) {
            telemetry.addData("Front Left Position", frontLeft.getCurrentPosition());
            telemetry.addData("Front Right Position", frontRight.getCurrentPosition());
            telemetry.addData("Back Left Position", backLeft.getCurrentPosition());
            telemetry.addData("Back Right Position", backRight.getCurrentPosition());
        } else {
            telemetry.addData("MecanumDrive: Unable to setup frontLeft, frontRight, backLeft, backRight motors", "");
        }
    }
}
