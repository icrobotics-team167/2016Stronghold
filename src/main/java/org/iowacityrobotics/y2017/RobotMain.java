package org.iowacityrobotics.y2017;

import org.iowacityrobotics.roboed.api.IRobot;
import org.iowacityrobotics.roboed.api.IRobotProgram;
import org.iowacityrobotics.roboed.api.RobotMode;
import org.iowacityrobotics.roboed.api.operations.IOpMode;
import org.iowacityrobotics.roboed.api.subsystem.ISubsystem;
import org.iowacityrobotics.roboed.api.vision.CameraType;
import org.iowacityrobotics.roboed.api.vision.ICameraServer;
import org.iowacityrobotics.roboed.api.vision.IImageProvider;
import org.iowacityrobotics.roboed.impl.data.DataMappers;
import org.iowacityrobotics.roboed.impl.subsystem.impl.DualJoySubsystem;
import org.iowacityrobotics.roboed.impl.subsystem.impl.DualTreadSubsystem;
import org.iowacityrobotics.roboed.util.collection.Pair;
import org.iowacityrobotics.roboed.util.math.Vector2;
import org.iowacityrobotics.roboed.util.robot.QuadraSpeedController;

public class RobotMain implements IRobotProgram {

    private ISubsystem<Void, Pair<Vector2, Vector2>> driveJoy, secJoy;
    private ISubsystem<DualTreadSubsystem.ControlDataFrame, Void> driveTrain;

    @Override
    public void init(IRobot robot) {
        // Set up camera server
        IImageProvider cam = robot.getCameraServer().getCamera(CameraType.USB, 0);
        robot.getCameraServer().putImageSource("USB Cam", cam);

        // Initialize subsystems
        driveJoy = robot.getSystemRegistry().getProvider(DualJoySubsystem.TYPE).getSubsystem(2);
        secJoy = robot.getSystemRegistry().getProvider(DualJoySubsystem.TYPE).getSubsystem(3);
        driveTrain = robot.getSystemRegistry()
                .getProvider(DualTreadSubsystem.TYPE_CUSTOM)
                .getSubsystem(QuadraSpeedController.ofCANTalons(1, 2, 8, 9));

        // Create teleop opmode
        IOpMode stdMode = robot.getOpManager().getOpMode("standard");
        stdMode.onInit(() -> driveTrain.bind(driveJoy.output()
                .map(DataMappers.invert())
                .map(DataMappers.throttle(() -> 0.75D))
                .map(DataMappers.dualJoyTank())));
        stdMode.whileCondition(() -> true);
        robot.getOpManager().setDefaultOpMode(RobotMode.TELEOP, "standard");
    }

}
