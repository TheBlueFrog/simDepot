package com.mike.market;

import com.mike.sim.Clock;

import java.util.ArrayList;
import java.util.List;

// this class collects the information about each open order
// at a given time
public class OpenOrder {
	
	static public enum Status {OpenForBids, BidRejected, BidAccepted };

	private final Long time;
	private final Order order;
	private Status orderStatus;
	private List<Bid> bidHistory = new ArrayList<>();

	public OpenOrder(Order order) {
		this.time = Clock.getTime();
		this.order = order;
		this.orderStatus = Status.OpenForBids;
	}
	
	public Order getOrder() {
		return order;
	}
	public Status getStatus() {
		return orderStatus;
	}
	public Long getTime() {
		return this.time;
	}
	
	public void addBid(Bid bid, boolean accepted) {
		bidHistory.add(bid);
		orderStatus = accepted ? Status.BidAccepted : Status.BidRejected;
	}
	
}
