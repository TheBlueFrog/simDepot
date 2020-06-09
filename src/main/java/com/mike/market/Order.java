package com.mike.market;

import com.mike.agents.Consumer;
import com.mike.agents.Supplier;
import com.mike.sim.LocatedAgent;
import com.mike.util.Location;

import java.util.List;
import java.util.Objects;

/*
	an Order is a request by a Customer for a certain quantity
	of a particular Item
 */
public class Order {
	static private Long ids = 1L;
	
	private final Long id;
	
	private final Consumer consumer;
	private final Item item;
	private final int quantity;
	
	public Order(Consumer consumer, Item item, int quantity) {
		this.id = ids++;
		
		this.consumer = consumer;
		this.item = item;
		this.quantity = quantity;
	}
	
	
	@Override
	public String toString() {
		return String.format("Order %d, consumer %d, item %d, quantity %d",
				id, consumer.getId(), item.getId(), this.quantity);
	}
	
	public Item getItem() {
		return item;
	}
	public int getQuantity() {
		return quantity;
	}
	
	public Consumer getConsumer() {
		return this.consumer;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Order order = (Order) o;
		return id.equals(order.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
