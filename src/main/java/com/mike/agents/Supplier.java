package com.mike.agents;

import com.mike.market.Item;
import com.mike.market.OpenOrders;
import com.mike.sim.Clock;
import com.mike.sim.Framework;
import com.mike.sim.Message;
import com.mike.util.Location;
import com.mike.util.Log;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Supplier extends OnHandAgent {
	private static final String TAG = Supplier.class.getSimpleName();
	
	// we have in-hand items, once a quantity of some item is ordered we
	// move it from in-hand to our ordered list.  keeps
	// us from selling it twice.  generally orders come
	// from a truck (who doesn't have it in-hand)

	private List<Item> myItems = new ArrayList<>();
	//private List<Item> ordered = new ArrayList<>();
	
    public Supplier(Framework framework, Long id) {
		super(framework, id);
	
		location = Location.getRandom(
				new Location((Location.MapWidth * 0.1) + Location.MapCenterX,
						(Location.MapHeight * 0.1) + Location.MapCenterY),
				Location.MapWidth * 0.2);

		myItems.add(new Item(this));
		
		register();
    }
	protected Supplier(Framework framework, Long id, boolean derived) {
		super(framework, id);
		
		location = Location.getRandom(
				new Location((Location.MapWidth * 0.1) + Location.MapCenterX,
						(Location.MapHeight * 0.1) + Location.MapCenterY),
				Location.MapWidth * 0.2);

		// don't register derived classes, let them do it
//		register();
	}
	
    @Override
	protected String getClassName() {
		return Supplier.class.getSimpleName();
	}
	
	@Override
	protected void paint(Graphics2D g2) {
    	int i = inHandItems.size() < 1 ? 0 : inHandItems.get(0).getQuantity();
		String label = String.format("%d: %d", getSerialNumber(), i);
		
		g2.setColor(Color.BLUE);
		
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
			
			send(new Message(this, Market.class, "active"));
			return;
		}

		if (msg.message instanceof OpenOrders) {
			// go through and see if we care?  raise/lower production?
		}
		
		if (msg.sender instanceof Clock) {
			// clock msg come to all Agents, the Clock also causes
			// a display refresh to be requested
			tick();
		}
	}
	
	private void tick() {
		if ((Clock.getTime() % 500) == 0L) {
			if (runningOut()) {
				// pick from the metaphorical tree of plenty
				Item item = myItems.get(0);
				addMore(item, 1);
				Log.d(TAG, String.format("%s created more of %s", this, item));
			}
		}
	}
	
	private boolean runningOut() {
		boolean b = false;
		if (inHandItems.size() == 0)
			return true;
		
		InHandItem inHandItem = inHandItems.get(0);
		if (inHandItem.getQuantity() < predictedDemand(inHandItem))
			return true;
		
		return false;
	}
	
	private int predictedDemand(InHandItem inHandItem) {
		return 2;
	}
	
	private void addMore(Item item, int i) {
		for(InHandItem inHandItem: inHandItems) {
			if (inHandItem.getItem().equals(item)) {
				inHandItem.incQuantity(1);
				return;
			}
		}
		
		inHandItems.add(new InHandItem(item, 1));
	}
	
//	public void order(Item item) {
//    	assert onHand.contains(item);
//
//		onHand.remove(item);
//		ordered.add(item);
//	}
	
	public List<InHandItem> getInHandItems() {
		return inHandItems;
	}
	
	@Override
	public String toString() {
		return String.format("Supplier {" +
					"id = %d, " +
					"myItems = %d, " +
					"inHandItems = %d} ",
					getId(),
					myItems.size(),
					inHandItems.size());
	}
}
