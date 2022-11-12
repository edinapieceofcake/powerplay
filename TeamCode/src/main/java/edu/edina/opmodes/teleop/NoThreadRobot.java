package edu.edina.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import edu.edina.library.subsystems.Intake2;
import edu.edina.library.subsystems.Lift2;
import edu.edina.library.subsystems.MecanumDrive;
import edu.edina.library.subsystems.Subsystem;
import edu.edina.library.util.RobotState;

public class NoThreadRobot {
    private List<Subsystem> subsystems;
    private Telemetry telemetry;
    public MecanumDrive drive;
    public Lift2 lift;
    public Intake2 intake;
    public RobotState robotState = new RobotState();

    public void update() {
        for (Subsystem subsystem : subsystems) {
            if (subsystem == null) continue;
            try {
                subsystem.update();
            } catch (Throwable t) {
                this.telemetry.addData("updating systems", "");
                this.telemetry.update();
            }
        }
    }

    public NoThreadRobot(OpMode opMode, Telemetry telemetry) {
        this.telemetry = telemetry;

        subsystems = new ArrayList<>();

        try {
            drive = new MecanumDrive(opMode.hardwareMap, robotState);
            subsystems.add(drive);
        } catch (IllegalArgumentException e) {

        }

        try {
            lift = new Lift2(opMode.hardwareMap, robotState);
            subsystems.add(lift);
        } catch (IllegalArgumentException e){

        }

        try {
            intake = new Intake2(opMode.hardwareMap, robotState);
            subsystems.add(intake);
        } catch (IllegalArgumentException e){

        }
    }

    public void telemetry()
    {
        robotState.telemetry(telemetry);
    }
}
