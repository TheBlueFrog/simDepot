package com.mike.market;

import com.mike.agents.Consumer;
import com.mike.agents.Supplier;

import java.util.Objects;

/**
 * an Item is a thing for sale by a supplier to customer
 *
 * it can be ordered by a consumer and will be delivered
 *
 */
public class Item {
	static private Long ids = 1L;
	
    private final Long id;
    private final Supplier supplier;
    
	private Consumer consumer = null;
	
	// an item 'in-stock' at a supplier
	public Item(Supplier supplier) {
        this.id = ids++;
        this.supplier = supplier;
    }
	
	public void setConsumer(Consumer consumer) {
		assert this.consumer == null;
		this.consumer = consumer;
	}

    public Long getId() {
        return this.id;
    }
    
    public void delete () {
		// ?
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Item item = (Item) o;
		return Objects.equals(getId(), item.getId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
	
	public Supplier getSupplier() {
		return this.supplier;
	}
	
	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", supplier=" + supplier.toString() +
				", consumer=" + consumer.toString() +
				'}';
	}
}
