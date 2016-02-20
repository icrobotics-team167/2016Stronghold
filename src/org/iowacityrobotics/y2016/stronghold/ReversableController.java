package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.util.Vector2;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ReversableController extends LogitechTankController {

	private boolean reverse, prevBtnStateReverse;
	
	public ReversableController(int port, boolean raaf) {
		super(port, raaf);
	}
	
	@Override
	public void update() {
		super.update();
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
	
	@Override
	public Vector2 getPosition() {
		if (!reverse)
			return new Vector2(this.getAxis(1), raaf ? -this.getAxis(4) : -this.getAxis(5));
		else
			return new Vector2(raaf ? -this.getAxis(4) : -this.getAxis(5), this.getAxis(1));
	}
	
}
