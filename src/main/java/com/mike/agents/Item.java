package com.mike.agents;

import java.util.Objects;

/**
 * an Item is a thing for sale by a supplier to customer
 *
 * for simplicity if a supplier has 9 dozen eggs for sale those
 * are 9 of the same items available.  If a customer orders 3 dozen
 * that is 3 distinct items to be moved from the the supplier to the
 * consumer.  This allows for items to be sourced from multiple
 * suppliers without any explicit effort.
 *
 * Items are created by suppliers and deleted by consumers
 *
 * suppliers, trucks and consumers all have an on-hand item list,
 * 	when created items are added to the supplier's on-hand list
 * 	when picked or dropped items are transferred from one on-hand list
 * 	to another
 *
 * 	eventually consumers will delete an item from their on-hand list
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
}
