package edu.edina.library.subsystems;


import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import edu.edina.library.util.DriveSpeed;
import edu.edina.library.util.RobotState;

public class MecanumDrive extends Subsystem{

    private double leftStickX;
    private double leftStickY;
    private double rightstickX;
    private RobotState robotState;
    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    private boolean speedChanged = false;

    private com.arcrobotics.ftclib.drivebase.MecanumDrive drive;

    public MecanumDrive(HardwareMap map, RobotState robotState){
        try {
            frontLeft = new Motor(map, "leftFront", Motor.GoBILDA.RPM_312);
            frontRight = new Motor(map, "rightFront", Motor.GoBILDA.RPM_312);
            backLeft = new Motor(map, "leftRear", Motor.GoBILDA.RPM_312);
            backRight = new Motor(map, "rightRear", Motor.GoBILDA.RPM_312);

            drive = new com.arcrobotics.ftclib.drivebase.MecanumDrive(frontLeft, frontRight, backLeft, backRight);
            drive.setMaxSpeed(1.3);
            robotState.DriveSpeed = DriveSpeed.Low;
            robotState.DriveSuccessfullySetup = true;
        } catch (Exception ex) {
            robotState.DriveSuccessfullySetup = false;
        }

        this.robotState = robotState;
    }

    @Override
    public void update() {
        if (speedChanged) {
            if (robotState.DriveSpeed == DriveSpeed.Fast) {
                drive.setMaxSpeed(1.3);
            } else if (robotState.DriveSpeed == DriveSpeed.Low) {
                drive.setMaxSpeed(0.5);
            } else {
                drive.setMaxSpeed(1);
            }

            speedChanged = false;
        }

        drive.driveRobotCentric(-leftStickX, leftStickY, -rightstickX);
    }

    public void setDriveProperties(double leftStickX, double leftStickY, double rightStickX,
                                   boolean driveSlow, boolean driveMedium, boolean driveFast) {
        this.leftStickX = leftStickX;
        this.leftStickY = leftStickY;
        this.rightstickX = rightStickX;

        if (driveSlow) {
            robotState.DriveSpeed = DriveSpeed.Low;
            speedChanged = true;
        } else if (driveMedium) {
            robotState.DriveSpeed = DriveSpeed.Medium;
            speedChanged = true;
        } else if (driveFast) {
            robotState.DriveSpeed = DriveSpeed.Fast;
            speedChanged = true;
        }
    }
}
