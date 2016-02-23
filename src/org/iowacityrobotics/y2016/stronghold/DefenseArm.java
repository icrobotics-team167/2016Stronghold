package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;
import org.iowacityrobotics.lib167.util.MathUtils;

import edu.wpi.first.wpilibj.CANTalon;

public class DefenseArm implements IComponent<Double> {
	
	private CANTalon armTalon;
	private final int port;
	private double val;
	
	public DefenseArm(int s) {
		armTalon = new CANTalon(s);
		this.port = s;
	}

	@Override
	public int getPort() {
		return port;
	}


	@Override
	public void setState(Double value) throws UnsupportedOperationException {
		val = MathUtils.clamp(value, -1D, 1D);
		armTalon.set(val);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return val;
	}

}
