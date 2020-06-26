package com.mike.market;

import com.mike.agents.Consumer;
import com.mike.agents.Supplier;

import java.util.Objects;

/**
 * an Item is a thing for sale by a supplier to customer
 *
 * it can be ordered by a consumer and will be delivered
 * items don't have quantity, they are abstract.  a consumer
 * buys some quantity of an item, trucks pickup a quantity
 * and deliver a quantity.  this allows a truck to pickup
 * 3 of an item from the supplier, use 2 it has in the truck
 * and deliver 5 to a consumer.  or pickup 10, deliver 5
 * and hold 5 in the truck for future orders
 *
 * not sure how to model item age yet
 */
public class Item {
	static private Long ids = 1L;
	
    private final Long id;
    private final Supplier supplier;
    
	// an item 'in-stock' at a supplier
	public Item(Supplier supplier) {
        this.id = ids++;
        this.supplier = supplier;
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
				", supplier=" + supplier +
				'}';
	}
}
