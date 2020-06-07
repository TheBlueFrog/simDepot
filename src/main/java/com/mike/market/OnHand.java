package com.mike.market;

import com.mike.market.Item;
import com.mike.sim.Agent;

public interface OnHand {
	
	/**
	 * an
 	 * @param item
	 * @param quantity
	 */
	public abstract void add(Item item, int quantity);
	public abstract void delete(Item item);
	
	public abstract void transfer(Agent from, Agent to);
}
