package com.mike.market;

import com.mike.agents.Supplier;
import com.mike.sim.Agent;

/**
 * this class captures a bid and it's status for an order
 */
public class Bid {
	private final Long bid;
	private final OpenOrder openOrder;
	private final Supplier supplier;
	
	public Bid(OpenOrder openOrder, Long bid, Supplier supplier) {
		this.openOrder = openOrder;
		this.bid = bid;
		this.supplier = supplier;
	}
	
	public OpenOrder getOpenOrder() {
		return openOrder;
	}
}
