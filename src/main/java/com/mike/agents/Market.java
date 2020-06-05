package com.mike.agents;

// this class is the marketplace, it has a list of items
// that can be ordered and it holds a list of ordered
// items that need to be scheduled for pick/drop

import com.mike.market.Bid;
import com.mike.market.Item;
import com.mike.market.OpenOrder;
import com.mike.market.Order;
import com.mike.sim.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Market extends Agent {
	
	private static final String TAG = Market.class.getSimpleName();
	private static Market theMarket = null;
	
	static private List<Supplier> suppliers = new ArrayList<>();

	// the current open orders and their bid history
	static private OpenOrders orders = new OpenOrders();

	static public Market getMarket() {
		return theMarket;
	}
	
	public Market (Framework framework, Long id) {
		super(framework, id);
		register();
		
		assert theMarket == null;
		theMarket = this;
	}
	
	// select a random item,
	public static Item selectItem() {
		List<Item> available = getAvailable();
		Item item = available.get(Main.getRandom().nextInt(available.size()));
		return item;
	}
	
	private static List<Item> getAvailable() {
		List<Item> available = new ArrayList<>();
		suppliers.forEach(supplier -> {
			available.addAll(supplier.getItems());
		});
		return available;
	}
	
	@Override
	public String toString() {
		return "Market{}";
	}
	
	public void order(Order order) {
		assert ! orders.contains(order);
		orders.add(order);
		
		// broadcast to suppliers
		OpenOrders oos = new OpenOrders(orders);
		suppliers.forEach(supplier -> send(supplier, oos));
	}
	
	@Override
	protected String getClassName() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.recipientSerialNumber == this.getSerialNumber();
		
		if ((msg.sender == null) && (((Framework.State) msg.message)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready, so
			// all suppliers etc. have registered
			return;
		}
		
		if (msg.sender instanceof Clock) {
			// clock msgs come to all Agents
		}
		if (msg.sender instanceof Supplier) {
			// something from a supplier, trucks are suppliers
			
			if ((msg.message instanceof String) && ((String) msg.message).equals("active")) {
				suppliers.add((Supplier) msg.sender);
			}
			else if ((msg.message instanceof String) && ((String) msg.message).equals("inactive")) {
				suppliers.remove((Supplier) msg.sender);
			}
		}

		if ((msg.message instanceof String) && ((String) msg.message).equals("openOrders")) {
			// somebody wants the open orders
			send(msg.sender, new OpenOrders(orders));
		}
		if (msg.message instanceof Bid) {
			// somebody bidding on an open order
			Bid bid = (Bid) msg.message;
			OpenOrder openOrder = bid.getOpenOrder();
			
			// accept or reject
			boolean ok = true;
			
			// add to history
			openOrder.addBid(bid, ok);

			// tell sender
			send(msg.sender, openOrder);
		}
	}

	
}
