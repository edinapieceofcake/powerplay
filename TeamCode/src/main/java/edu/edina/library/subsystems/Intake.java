package edu.edina.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake extends Subsystem{

    private DcMotorEx rightmotor;
    private DcMotorEx leftmotor;
    private double intakespeed;

    public Intake(HardwareMap map){
        rightmotor = map.get(DcMotorEx.class, "rightmotor");
        leftmotor = map.get(DcMotorEx.class, "leftmotor");
    }

    @Override
    public void update() {
        rightmotor.setPower(intakespeed);
        leftmotor.setPower(intakespeed);
    }

    public void SetSpeed(double lefttrigger, double righttrigger){
        if (righttrigger != 0 && lefttrigger!= 0) {
            intakespeed = 0;
        } else if (righttrigger != 0) {
            intakespeed = righttrigger;
        } else if (lefttrigger != 0) {
            intakespeed = lefttrigger;
        } else {
            intakespeed = 0;
        }
    }

}
