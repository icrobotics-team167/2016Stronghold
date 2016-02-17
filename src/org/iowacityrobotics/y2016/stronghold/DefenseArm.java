package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;

public class DefenseArm implements IComponent<Double> {
	
	private Talon armTalon;
	private final int port;
	
	public DefenseArm(int s) {
		armTalon = new Talon(s);
		this.port = s;
	}

	@Override
	public int getPort() {
		return port;
	}


	@Override
	public void setState(Double val) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	
}
