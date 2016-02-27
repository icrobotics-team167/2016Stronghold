package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.util.Vector2;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ReversableController extends LogitechTankController {

	private boolean reverse, prevBtnStateReverse;
	private ThrottleState thState = ThrottleState.NORMAL;
	
	public ReversableController(int port, boolean raaf) {
		super(port, raaf);
	}
	
	@Override
	public void update() {
		if (isPressed(1))
			thState = ThrottleState.SLOW;
		else if (isPressed(3))
			thState = ThrottleState.NORMAL;
		else if (isPressed(4))
			thState = ThrottleState.FAST;
		SmartDashboard.putString("Speed", thState.name());
		
		if (isPressed(2)) {
			if (!prevBtnStateReverse) {
				reverse = !reverse;
				prevBtnStateReverse = true;
			}
		}
		else
			prevBtnStateReverse = false;
		SmartDashboard.putBoolean("Reverse", reverse);
	}
	
	public double getThrottle() {
		return thState.mult;
	}
	
	@Override
	public boolean isTurboActive() {
		return false;
	}
	
	@Override
	public Vector2 getPosition() {
		if (!reverse)
			return new Vector2(this.getAxis(1), raaf ? -this.getAxis(4) : -this.getAxis(5));
		else
			return new Vector2(raaf ? -this.getAxis(4) : -this.getAxis(5), this.getAxis(1));
	}
	
	public static enum ThrottleState {
		
		SLOW(0.64D),
		NORMAL(0.75D),
		FAST(0.98D);
		
		public final double mult;
		
		private ThrottleState(double speedMultiplier) {
			this.mult = speedMultiplier;
		}
		
	}
	
}
