package com.mike.agents;

import com.mike.sim.Clock;
import com.mike.sim.Framework;
import com.mike.sim.LocatedAgent;
import com.mike.sim.Message;
import com.mike.util.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Supplier extends LocatedAgent {

    public Supplier(Framework framework, Long id) {
		super(framework, id);
		register();
	
		location = Location.getRandom(
				new Location(Location.MapWidth * 0.1, Location.MapHeight * 0.1),
				Location.MapWidth * 0.2);
    }
	@Override
	protected void paint(Graphics2D g2) {
		String label = String.format("%d: %d", getSerialNumber(), 0);
		
		g2.setColor(Color.BLUE);
		
		g2.drawString(
				label,
				(int) location.x, (int) location.y);
		
		g2.drawRect(
				(int) location.x, (int) location.y,
				5, 5);
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.serialNumber == this.getSerialNumber();
		
		if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready
			return;
		}
		
		if (msg.mSender instanceof Clock) {
			// clock msg come to all Agents, the Clock also causes
			// a display refresh to be requested
			tick();
		}
	}
	
	private void tick() {
//		if (location.y < Location.MapTop) {
//			location.y += 1;
//		}
//		else {
//			location.y = Location.MapBottom;
//		}
	}
}
