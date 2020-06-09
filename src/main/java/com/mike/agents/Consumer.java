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
		
		location = Location.getRandom(
				new Location((Location.MapWidth * 0.5) + Location.MapCenterX,
						(Location.MapHeight * 0.2) + Location.MapCenterY),
				Location.MapWidth * 0.1);

		register();
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
				(int) location.x, (int) (Location.MapHeight - location.y));
		
		g2.drawRect(
				(int) location.x, (int) (Location.MapHeight - location.y),
				5, 5);
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.recipientSerialNumber == this.getSerialNumber();
		
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
		if ((Clock.getTime() % 1000) == 0L) {
			InHandItem desired = Market.selectItem();
			desired.setQuantity(Main.getRandom().nextInt(desired.getQuantity() - 1) + 1);
			
			if ( ! haveOnHand(desired)) {
				// TODO could get fancy and only order what we need
				Order order = new Order(this, desired.getItem(), desired.getQuantity());
				Market.getMarket().order(order);
				
				Log.d(TAG, String.format("%s ordered %s", this.toString(), order.toString()));
			}
			else {
				// we used from our stock, update it
				drop(desired.getItem(), desired.getQuantity());

				Log.d(TAG, String.format("%s used from stock %d %s", this.toString(), desired.getQuantity(), desired.getItem().toString()));
			}
		}
	}
	
	private boolean haveOnHand(InHandItem desired) {
		for (InHandItem inHandItem : inHandItems) {
			if (inHandItem.getItem().equals(desired.getItem())
					&& inHandItem.getQuantity() >= desired.getQuantity())
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("Consumer %d", this.getId());
	}
}
