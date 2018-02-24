package org.iowacityrobotics.y2016;

import org.iowacityrobotics.roboed.data.Data;
import org.iowacityrobotics.roboed.data.Funcs;
import org.iowacityrobotics.roboed.data.mapper.Mapper;
import org.iowacityrobotics.roboed.data.sink.Sink;
import org.iowacityrobotics.roboed.data.source.Source;
import org.iowacityrobotics.roboed.robot.Flow;
import org.iowacityrobotics.roboed.robot.IRobotProgram;
import org.iowacityrobotics.roboed.robot.RobotMode;
import org.iowacityrobotics.roboed.subsystem.MapperSystems;
import org.iowacityrobotics.roboed.subsystem.SinkSystems;
import org.iowacityrobotics.roboed.subsystem.SourceSystems;
import org.iowacityrobotics.roboed.util.math.Maths;
import org.iowacityrobotics.roboed.util.math.Vector2;
import org.iowacityrobotics.roboed.util.robot.MotorTuple4;

public class Robot implements IRobotProgram {


    @Override
    public void init() {
        Source<Double> joyLeft = SourceSystems.CONTROL.axis(Nums.CTRL_L, Nums.AXIS_Y)
                .map(MapperSystems.CONTROL.deadZoneD(0.1D));
        Source<Double> joyRight = SourceSystems.CONTROL.axis(Nums.CTRL_R, Nums.AXIS_Y)
                .map(MapperSystems.CONTROL.deadZoneD(0.1D));
        Source<Double> throttle = SourceSystems.CONTROL.axis(Nums.CTRL_R, 3)
                .map(Data.mapper(d -> (1 - d) / 2D));
        Source<Boolean> reverse = SourceSystems.CONTROL.button(Nums.CTRL_R, 2)
                .map(MapperSystems.CONTROL.toggle());
        Source<Vector2> srcDrive = joyLeft.inter(joyRight, Data.inter((l, r) -> new Vector2(l, r)))
                .inter(throttle, Data.inter((v, t) -> v.multiply(-t)))
                .inter(reverse, Data.inter((v, r) -> {
                    if (r) {
                        double x = -v.y();
                        v.y(-v.x()).x(x);
                    }
                    return v;
                }));
        MotorTuple4 motors = MotorTuple4.ofTalons(1, 8, 2, 9);
        Sink<Vector2> drive = SinkSystems.DRIVE.dualTread(motors);

        Source<Double> ballBeltCtrl = SourceSystems.CONTROL.button(Nums.CTRL_L, 2)
                .map(new BallBeltController());
        Source<Double> triggerPrefire = SourceSystems.CONTROL.axis(Nums.CTRL_L, Nums.THROTTLE)
                .map(Data.mapper(d -> (1 - d) / 4D + 0.5D))
                .inter(SourceSystems.CONTROL.button(Nums.CTRL_L, 1), Data.inter((v, t) -> t ? v : 0));
        Source<Double> triggerFire = SourceSystems.CONTROL.button(Nums.CTRL_R, 1)
                .map(MapperSystems.CONTROL.buttonValue(0D, -65D));
        Source<Double> srcBallBelt = ballBeltCtrl.inter(triggerFire, Data.inter((a, b) -> Maths.clamp(a + b, -1D, 1D)));

        Sink<Double> ballBelt = SinkSystems.MOTOR.talonSrx(4).join(SinkSystems.MOTOR.talonSrx(6));
        Sink<Double> fireWheels = SinkSystems.MOTOR.talonSrx(3).join(SinkSystems.MOTOR.talonSrx(7).map(Funcs.invertD()));

        RobotMode.TELEOP.setOperation(() -> {
            drive.bind(srcDrive);
            ballBelt.bind(srcBallBelt);
            fireWheels.bind(triggerPrefire);
            Flow.waitInfinite();
        });
    }

    private class BallBeltController extends Mapper<Boolean, Double> {
        private long stop = 0L;
        @Override public Double apply(Boolean data) {
            long time = System.currentTimeMillis();
            if (data) {
                stop = time + 100L;
                return -0.5D;
            } else if (time < stop) {
                return 0.2D;
            }
            return 0D;
        }
    }

}
