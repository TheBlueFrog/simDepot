package com.mike.market;

import java.util.ArrayList;
import java.util.List;

/**
 * wrapper around List<OpenOrder> since it's hard to use
 * with instanceof
 */
public class OpenOrders {

	private List<OpenOrder> openOrders = new ArrayList<>();
	
	public OpenOrders() {
	}
	
	/** copy constructor */
	public OpenOrders(OpenOrders orders) {
		this.openOrders = new ArrayList<>(orders.getList());
	}
	
	public List<OpenOrder> getList() {
		return openOrders;
	}
	
	public boolean contains(Order order) {
		return openOrders.contains(order);
	}
	
	public void add(Order order) {
		openOrders.add(new OpenOrder(order));
	}
	
	public void get(OpenOrder openOrder) {
	
	}
}
