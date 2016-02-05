package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;

import edu.wpi.first.wpilibj.Servo;

public class DefenseArm implements IComponent<Boolean> {
	
	private Servo armServo;
	private final int port;
	
	public DefenseArm(int s) {
		armServo = new Servo(s);
		this.port = s;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setState(Boolean state) throws UnsupportedOperationException {
		armServo.set(state ? 1 : 0);
	}

	@Override
	public Boolean getState() throws UnsupportedOperationException {
		return armServo.get() > 0.2 ? true : false;
	}
	
}
