package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;

public class ShootDrive implements IComponent<Double> {
	
	private CANTalon[] talons;
	private double speed;
	
	public ShootDrive(int l, int r) {
		talons =new CANTalon[] {new CANTalon(l), new CANTalon(r)};
	}
	
	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public void setState(Double speed) throws UnsupportedOperationException {
		for (CANTalon t : talons)
			t.set(0.9D * speed);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return speed;
	}

}
