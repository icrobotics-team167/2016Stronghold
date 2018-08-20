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
        Source<Double> joyLeft = SourceSystems.CONTROL.axis(Nums.CTRL, Nums.AXIS_Y1)
                .map(MapperSystems.CONTROL.deadZoneD(0.1D))
                .map(Data.mapper(d -> d * 0.8D));
        Source<Double> joyRight = SourceSystems.CONTROL.axis(Nums.CTRL, Nums.AXIS_Y2)
                .map(MapperSystems.CONTROL.deadZoneD(0.1D))
                .map(Data.mapper(d -> d * 0.8D));
//        Source<Double> throttle = SourceSystems.CONTROL.axis(Nums.CTRL, Nums.THROTTLE)
//                .map(Data.mapper(d -> (1 - d) / 2D));
        Source<Boolean> reverse = SourceSystems.CONTROL.button(Nums.CTRL, Nums.REVERSE)
                .map(MapperSystems.CONTROL.toggle());
        Source<Vector2> srcDrive = joyLeft.inter(joyRight, Data.inter(Vector2::new))
                .inter(/*throttle*/Data.source(() -> 1D), Data.inter((v, t) -> v.multiply(-t)))
                .inter(reverse, Data.inter((v, r) -> {
                    if (r) {
                        double x = -v.y();
                        v.y(-v.x()).x(x);
                    }
                    return v;
                }));
        MotorTuple4 motors = MotorTuple4.ofTalons(1, 8, 2, 9);
        Sink<Vector2> drive = SinkSystems.DRIVE.dualTread(motors);

        Source<Double> ballBeltCtrl = SourceSystems.CONTROL.axis(Nums.CTRL, Nums.BELT_CTRL)
                .map(MapperSystems.CONTROL.booleanify(0.5D))
                .map(new BallBeltController());
        Source<Double> prefireThrottle = SourceSystems.CONTROL.axis(Nums.CTRL, Nums.FIRE_LEVER);
        Source<Double> triggerPrefire = prefireThrottle
                .map(MapperSystems.CONTROL.deadZoneD(0.2D))
//                .map(Data.mapper(d -> (1 - d) / 4D + 0.5D))
                /*.inter(SourceSystems.CONTROL.button(Nums.CTRL_L, 1), Data.inter((v, t) -> t ? v : 0))*/;
        Source<Double> triggerFire = SourceSystems.CONTROL.button(Nums.CTRL, Nums.TRIGGER)
                .map(MapperSystems.CONTROL.buttonValue(0D, -65D));
        Source<Double> srcBallBelt = ballBeltCtrl.inter(triggerFire, Data.inter((a, b) -> Maths.clamp(a + b, -1D, 1D)));

        Sink<Double> ballBelt = SinkSystems.MOTOR.talonSrx(4).join(SinkSystems.MOTOR.talonSrx(6));
        Sink<Double> fireWheels = SinkSystems.MOTOR.talonSrx(3).join(SinkSystems.MOTOR.talonSrx(7).map(Funcs.invertD()));

        Sink<Double> dashThrottle = SinkSystems.DASH.number("Throttle");
        Sink<Double> dashShootMagn = SinkSystems.DASH.number("Cannon Magnitude");

        RobotMode.TELEOP.setOperation(() -> {
            drive.bind(srcDrive);
            ballBelt.bind(srcBallBelt);
            fireWheels.bind(triggerPrefire);
//            dashThrottle.bind(throttle);
            dashShootMagn.bind(prefireThrottle.map(Data.mapper(d -> (1 - d) / 2D)));
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
                return 0.25D;
            }
            return 0D;
        }
    }

}
