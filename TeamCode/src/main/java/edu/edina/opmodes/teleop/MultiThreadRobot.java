package edu.edina.opmodes.teleop;

import edu.edina.library.subsystems.Intake2;
import edu.edina.library.subsystems.Lift2;
import edu.edina.library.subsystems.MecanumDrive;
import edu.edina.library.subsystems.Subsystem;
import edu.edina.library.util.RobotState;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MultiThreadRobot {
    private ExecutorService subsystemUpdateExecutor;
    private boolean started;

    private List<Subsystem> subsystems;

    private Telemetry telemetry;
    public MecanumDrive drive;
    public Lift2 lift;
    public Intake2 intake;
    public RobotState robotState = new RobotState();

    private Runnable subsystemUpdateRunnable = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Subsystem subsystem : subsystems) {
                    if (subsystem == null) continue;
                    try {
                        subsystem.update();
                    } catch (Throwable t) {
                        this.telemetry.addData("Exception running thread 1", "");
                        this.telemetry.update();
                    }
                }
            } catch (Throwable t) {
                this.telemetry.addData("Exception running thread 2", "");
                this.telemetry.update();
            }
        }
    };

    public MultiThreadRobot(OpMode opMode, Telemetry telemetry) {
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

        subsystemUpdateExecutor = ThreadPool.newSingleThreadExecutor("subsystem update");
    }

    public void start() {
        if (!started) {
            subsystemUpdateExecutor.submit(subsystemUpdateRunnable);
            started = true;
        }
    }

    public void stop() {
        if (subsystemUpdateExecutor != null) {
            subsystemUpdateExecutor.shutdownNow();
            subsystemUpdateExecutor = null;
            started = false;
        }
    }

    public void telemetry()
    {
        robotState.telemetry(telemetry);
    }
}
