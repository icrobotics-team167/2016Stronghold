package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;

public class BallBelt implements IComponent<Double> {

	private CANTalon LTalon;
	private CANTalon RTalon;
	private double val;
	
	public BallBelt(int l, int r) {
		LTalon = new CANTalon(l);
		RTalon = new CANTalon(r);
	}
	
	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public void setState(Double value) throws UnsupportedOperationException {
		val = value;
			LTalon.set(val);
			RTalon.set(val);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return val;
	}

}