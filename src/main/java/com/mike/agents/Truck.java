package com.mike.agents;

import com.mike.market.*;
import com.mike.sim.Clock;
import com.mike.sim.Framework;
import com.mike.sim.LocatedAgent;
import com.mike.sim.Message;
import com.mike.util.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
	
	// the list of OpenOrder objects we have contracted to deliver
	private List<OpenOrder> myOpenOrders = new ArrayList<>();
	
	private LocatedAgent destination = null;
	private double heading;
	
	// something like center to edge in 3 hours, in map units per hour
	private double speedUPH = Location.MapHeight / 2.0 / 30.0;
	
	// a clock tick is 1 simulated minute
	private double maxDistancePerTick = speedUPH / 60.0;

	
	public Truck(Framework framework, Long id) {
		super(framework, id);
	
		location = Location.getRandom(
				new Location((Location.MapWidth * 0.2) + Location.MapCenterX,
						(Location.MapHeight * 0.2) + Location.MapCenterY),
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
					
					myOpenOrders.add(openOrder);
					destination = route();
				}
			}
		}
	}
	
	private void tick() {
		if (destination != null) {
			double[] d = { 0, 0 };
			calcStep(d);
			
			if ((Math.abs(d[0]) < 0.1) && (Math.abs(d[1]) < 0.1)) {
				// we have arrived
				// pickup and head out
				Order order = myOpenOrders.get(0).getOrder();
				Item item = order.getItem();
				
				// remove from supplier
				item.getSupplier().drop(item, order.getQuantity());
				// put in truck
				pick(item, order.getQuantity());
				
				destination = route();
				
				calcStep(d);
			}
			location.x += d[0];
			location.y += d[1];
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
	
	private void calcStep(double[] d) {
		double dx = destination.getLocation().x - location.x;
		double dy = destination.getLocation().y - location.y;
		heading = Math.atan2(dy, dx);
		double distance = Math.sqrt((dy * dy) + (dx * dx));
		
		if (distance > maxDistancePerTick)
			distance = maxDistancePerTick;
		
		d[0] = Math.cos(heading) * distance;
		d[1] = Math.sin(heading) * distance;
	}
	
	/** compute a route given the bids we have won and
		what we have in-hand
		we only go to Consumers, Suppliers and Trucks
	 */
	private LocatedAgent route() {
		// do we have everything in-hand
		List<OpenOrder> missing = getMissing();
		if (missing.size() > 0) {
			return missing.get(0).getOrder().getItem().getSupplier();
		}
		
		if (myOpenOrders.size() > 0)
			// TODO this precludes truck to truck handoff, or does the second
			// truck create an order, then Order can't be limited to Customer
			return myOpenOrders.get(0).getOrder().getConsumer();
			
		return null;
	}
	
	private List<OpenOrder> getMissing() {
		List<OpenOrder> missing = new ArrayList<>();
		for(OpenOrder openOrder : myOpenOrders) {
			Order order = openOrder.getOrder();
			Item item = order.getItem();
			if ( ! canSatisfy(order)) {
				missing.add(openOrder);
			}
		}
		return missing;
	}
	
	private boolean canSatisfy(Order order) {
		for(InHandItem inHandItem : inHandItems) {
			if (inHandItem.getItem().equals(order.getItem())
					&& inHandItem.getQuantity() >= order.getQuantity())
				return true;
		}
		
		return false;
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
