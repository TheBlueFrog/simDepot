package com.mike.agents;

import com.mike.market.Bid;
import com.mike.market.OpenOrder;
import com.mike.market.OpenOrders;
import com.mike.sim.Clock;
import com.mike.sim.Framework;
import com.mike.sim.LocatedAgent;
import com.mike.sim.Message;
import com.mike.util.Location;

import java.awt.*;

/**
 * trucks act like a supplier to consumers and as a consumer to
 * suppliers.
 *
 * trucks pick items from a supplier
 *
 * trucks drops items at the consumer
 *
 * trucks continually try to compute an optimal route to
 * 		drop items on-board at the consumer
 * 		pick items that are not on-board from the supplier
 *
 *
 */
public class Truck extends Supplier {
	
	private static final String TAG = Truck.class.getSimpleName();
	private LocatedAgent destination = null;
	private double heading;
	
	public Truck(Framework framework, Long id) {
		super(framework, id);
	
		location = Location.getRandom(
				new Location(Location.MapWidth * 0.2, Location.MapHeight * 0.2),
				Location.MapWidth * 0.1);

		register();
    }
	
	@Override
	protected String getClassName() {
		return Truck.class.getSimpleName();
	}
	
	@Override
	protected void paint(Graphics2D g2) {
		String label = String.format("%d: %d", getSerialNumber(), 0);
		
		g2.setColor(Color.GREEN);
		
		g2.drawString(
				label,
				(int) location.x, (int) location.y);
		
		g2.drawRect(
				(int) location.x, (int) location.y,
				5, 5);
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.recipientSerialNumber == this.getSerialNumber();
		
		if ((msg.sender == null) && (((Framework.State) msg.message)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready

			// get list of open orders
			send(new Message( this, Market.class, "openOrders"));
			return;
		}
		
		if (msg.sender instanceof Clock) {
			// clock msg come to all Agents, the Clock also causes
			// a display refresh to be requested
			tick();
			return;
		}

		if (msg.sender instanceof Market) {
			if (msg.message instanceof OpenOrders) {
				// all open orders, see if we want to bid on anything
				
				// hack bid on everything available
				OpenOrders openOrders = (OpenOrders) msg.message;
				openOrders.getList().forEach(order -> {
					if (order.getStatus() == OpenOrder.Status.OpenForBids) {
						// bid on it
						Bid bid = new Bid(order, 10L, this);
						send(msg.sender, bid);
					}
				});
			}
			else if (msg.message instanceof OpenOrder) {
				// bid response
				OpenOrder openOrder = (OpenOrder) msg.message;
				if (openOrder.getStatus().equals(OpenOrder.Status.BidAccepted)) {
					destination = openOrder.getOrder().getItem().getSupplier();
				}
			}
		}
	}
	
	private void tick() {
		if (destination != null) {
			double dx = location.x - destination.getLocation().x;
			double dy = location.y - destination.getLocation().y;
			heading = Math.atan2(dy, dx);
			double distance = 5.0; // derived from speed
			dx = Math.cos(heading * distance);
			dy = Math.sin(heading * distance);
			
			if ((Math.abs(dx) < 0.1) && (Math.abs(dy) < 0.1)) {
				// we have arrived
				// pickup and head out
				int i = 0;
			}
			location.x += dx;
			location.y += dy;
		}
		else {
			// wander about
			if (location.y < Location.MapTop) {
				location.y += 1;
			} else {
				location.y = Location.MapBottom;
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Truck {" +
						"id = %d, " +
						"inHandItems = %d" + '}',
				getId(),
				inHandItems.size());
	}
}
