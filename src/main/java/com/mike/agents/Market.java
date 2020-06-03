package com.mike.agents;

// this class is the marketplace, it has a list of items
// that can be ordered and it holds a list of ordered
// items that need to be scheduled for pick/drop

import com.mike.sim.*;
import com.mike.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Market extends Agent {
	
	private static final String TAG = Market.class.getSimpleName();
	
	static private List<Supplier> suppliers = new ArrayList<>();

	static private List<Item> ordered = new ArrayList<>();
	
	public static void register(Supplier supplier) {
		suppliers.add(supplier);
	}

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
	
	public static void order(Consumer consumer, Item item) {
		List<Item> available = getAvailable();
		assert available.contains(item);
		
		item.setConsumer(consumer);
		
		// it is possible to order an item more than once...
		
		Log.d(TAG, String.format("%s ordered %s", consumer.toString(), item.toString()));
		ordered.add(item);
	}
	
	public Market (Framework framework, Long id) {
		super(framework, id);
	}
	
	@Override
	protected String getClassName() {
		return null;
	}
	
	@Override
	protected void onMessage(Message msg) {
		
		assert msg.serialNumber == this.getSerialNumber();
		
		if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
			// frameworks says everyone is ready, so
			// all suppliers etc. have registered
			return;
		}
		
		if (msg.mSender instanceof Clock) {
			// clock msgs come to all Agents
		}
	}

	
}
