package edu.edina.library.subsystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class Subsystem {
    public abstract void update();

    public abstract void telemetry(Telemetry telemetry);
}
