package com.mike.agents;

// this class is the marketplace, it has a list of items
// that can be ordered and it holds a list of ordered
// items that need to be scheduled for pick/drop

import com.mike.market.Item;
import com.mike.market.OpenOrders;
import com.mike.market.Order;
import com.mike.sim.*;

import java.util.ArrayList;
import java.util.List;

public class Market extends Agent {
	
	private static final String TAG = Market.class.getSimpleName();
	
	static private List<Supplier> suppliers = new ArrayList<>();

	static private List<Order> orders = new ArrayList<>();
	
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
	
	public static void order(Order order) {
		orders.add(order);
	}
	
	public Market (Framework framework, Long id) {
		super(framework, id);
		register();
	}
	
	@Override
	protected String getClassName() {
		return null;
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.targetSerialNumber == this.getSerialNumber();
		
		if ((msg.sender == null) && (((Framework.State) msg.message)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready, so
			// all suppliers etc. have registered
			return;
		}
		
		if (msg.sender instanceof Clock) {
			// clock msgs come to all Agents
		}
		else if (msg.sender instanceof Supplier) {
			if ((msg.message instanceof String) && ((String) msg.message).equals("active")) {
				suppliers.add((Supplier) msg.sender);
			}
			else if ((msg.message instanceof String) && ((String) msg.message).equals("inactive")) {
				suppliers.remove((Supplier) msg.sender);
			}
		}
		else if (msg.message instanceof OpenOrders) {
			// somebody want all open orders
			send(new Message( this, msg.sender.getClass(), this.getSerialNumber(), new OpenOrders()));
			return;
		}
	}

	
}
