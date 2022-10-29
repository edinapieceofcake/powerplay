//I, Ben am just doing this for experience
package edu.edina.opmodes.teleop;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;

import edu.edina.library.subsystems.Subsystem;

public class Reach extends Subsystem {
    private boolean dpad_up;
    private boolean dpad_down;

    public Reach(HardwareMap hardwareMap)
    {
        Motor reachMotor = new Motor(hardwareMap, "reachMotor", Motor.GoBILDA.RPM_435);
    }


    @Override
    public void update() {

    }
}
