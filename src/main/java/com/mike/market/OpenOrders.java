package com.mike.market;

import com.mike.sim.Clock;

import java.util.ArrayList;
import java.util.List;

public class OpenOrders {
	
	private final ArrayList<Order> orders;
	private final Long time;
	
	public OpenOrders(List<Order> orders) {
		this.time = Clock.getTime();
		this.orders = new ArrayList<>(orders);
	}
}
