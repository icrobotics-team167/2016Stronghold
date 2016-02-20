package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.component.IComponent;
import org.iowacityrobotics.lib167.util.MathUtils;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DefenseArm implements IComponent<Double> {
	
	private Talon armTalon;
	private DigitalInput top, bot;
	private final int port;
	private double val;
	
	public DefenseArm(int s, int topLim, int botLim) {
		armTalon = new Talon(s);
		top = new DigitalInput(topLim);
		bot = new DigitalInput(botLim);
		this.port = s;
	}

	@Override
	public int getPort() {
		return port;
	}


	@Override
	public void setState(Double value) throws UnsupportedOperationException {
		SmartDashboard.putBoolean("top", top.get());
		SmartDashboard.putBoolean("bot", bot.get());
		val = MathUtils.clamp(value, top.get() ? -1D : 0D, bot.get() ? 1D : 0D);
		armTalon.set(val);
	}

	@Override
	public Double getState() throws UnsupportedOperationException {
		return val;
	}

}
