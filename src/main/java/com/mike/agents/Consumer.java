package com.mike.agents;

import com.mike.market.Item;
import com.mike.market.Order;
import com.mike.sim.*;
import com.mike.util.Location;
import com.mike.util.Log;

import java.awt.*;

public class Consumer extends OnHandAgent {
	
	private static final String TAG = Consumer.class.getSimpleName();
	
	public Consumer(Framework framework, Long id) {
        super(framework, id);
		register();
		
		location = Location.getRandom(
				new Location(Location.MapWidth * 0.5, Location.MapHeight * 0.2),
				Location.MapWidth * 0.1);
	}
	
	@Override
	protected String getClassName() {
		return Consumer.class.getSimpleName();
	}

	@Override
	protected void paint(Graphics2D g2) {
		String label = String.format("%d: %d", getSerialNumber(), 0);
		
		g2.setColor(Color.RED);
		
		g2.drawString(
				label,
				(int) location.x, (int) location.y);
		
		g2.drawRect(
				(int) location.x, (int) location.y,
				5, 5);
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.targetSerialNumber == this.getSerialNumber();
		
		if ((msg.sender == null) && (((Framework.State) msg.message)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready
			return;
		}
		
		if (msg.sender instanceof Clock) {
			// clock msg come to all Agents, the Clock also causes
			// a display refresh to be requested
			tick();
		}
	}
	
	private void tick() {

		// order items periodically
		if ((Clock.getTime() % 5000) == 0L) {
			Item item = Market.selectItem();
			int quantity = 1;
			Order order = new Order(this, item, quantity);
			Market.order(order);

			Log.d(TAG, String.format("%s ordered %s", this.toString(), order.toString()));
		}
	}
	
	@Override
	public String toString() {
		return String.format("Consumer %d", this.getId());
	}
}
