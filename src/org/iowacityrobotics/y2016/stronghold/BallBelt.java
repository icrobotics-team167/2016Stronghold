package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;

import edu.wpi.first.wpilibj.CANTalon;

public class BallBelt implements IComponent<Double> {

	private CANTalon[] talons;
	private double val;
	
	public BallBelt(int l, int r) {
		talons = new CANTalon[] {new CANTalon(l), new CANTalon(r)};
	}
	
	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public void setState(Double value) throws UnsupportedOperationException {
		val = value;
		for (CANTalon talon : talons)
			talon.set(val);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return val;
	}

}