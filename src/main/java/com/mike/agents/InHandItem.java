package com.mike.agents;

import com.mike.market.Item;
import com.mike.sim.Agent;

public class InHandItem {
	
	/**
	 * an agent has an Item and that collection
	 * has properties, mostly a quantity
	 *
	 * I have 3 of Item X
	 *
	 * we may add a time to this so we know how old
	 * this batch of milk is
	 */
	
	private int quantity;
	private final Item item;
	
	public InHandItem (Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void incQuantity(int quantity) {
		this.quantity += quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void decQuantity(int quantity) {
		assert this.quantity >= quantity;
		this.quantity -= quantity;
	}
}
