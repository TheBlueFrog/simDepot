package com.mike.agents;

import com.mike.market.Item;

public class Delivery {
	private final Item item;
	private final int quantity;
	
	public Delivery(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public Item getItem() {
		return this.item;
	}
}
