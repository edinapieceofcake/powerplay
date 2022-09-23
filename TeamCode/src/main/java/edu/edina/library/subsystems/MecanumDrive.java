package edu.edina.library.subsystems;


import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrive extends Subsystem{

    private double leftStickX;
    private double leftStickY;
    private double rightstickY;

    private com.arcrobotics.ftclib.drivebase.MecanumDrive drive;

    public MecanumDrive(HardwareMap map){

        Motor frontLeft = new Motor(map, "frontLeft", Motor.GoBILDA.RPM_435);
        Motor frontRight = new Motor(map, "frontRight", Motor.GoBILDA.RPM_435);
        Motor backLeft = new Motor(map, "backLeft", Motor.GoBILDA.RPM_435);
        Motor backRight = new Motor(map, "backRight", Motor.GoBILDA.RPM_435);



        drive = new com.arcrobotics.ftclib.drivebase.MecanumDrive(frontLeft, frontRight, backLeft, backRight);

    }

    @Override
    public void update() {

        drive.driveRobotCentric(leftStickX, leftStickY, rightstickY);

    }

    public void setVelocity(double leftStickX, double leftStickY, double rightStickY) {

        this.leftStickX = leftStickX;
        this.leftStickY = leftStickY;
        this.rightstickY = rightStickY;

    }


}
