package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift extends Subsystem{

    private DcMotorEx liftmotor;
    private double liftSpeed;

    public Lift(HardwareMap map){
        liftmotor = map.get(DcMotorEx.class, "liftMotor");
    }

    @Override
    public void update() {
        liftmotor.setPower(liftSpeed);
    }

    public void SetSpeed(double liftSpeed) {
        this.liftSpeed = liftSpeed;
    }
}
