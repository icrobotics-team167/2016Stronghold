package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;
import org.iowacityrobotics.lib167.util.MathUtils;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class BallBelt implements IComponent<Double> {

	private static final double LS_THRESHOLD = 0.3D;
	private CANTalon[] talons;
	private DigitalInput limit;
	private double val;
	
	public BallBelt(int l, int r, int limitId) {
		talons = new CANTalon[] {new CANTalon(l), new CANTalon(r)};
		limit = new DigitalInput(limitId);
	}
	
	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public void setState(Double value) throws UnsupportedOperationException {
		val = MathUtils.clamp(value, -1D, limit.get() ? 1D : 0D);
		for (CANTalon talon : talons)
			talon.set(val);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return val;
	}

}