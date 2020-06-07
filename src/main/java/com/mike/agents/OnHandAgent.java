package com.mike.agents;

import com.mike.market.Item;
import com.mike.sim.Framework;
import com.mike.sim.LocatedAgent;

import java.util.ArrayList;
import java.util.List;

abstract public class OnHandAgent extends LocatedAgent {
	
	protected List<InHandItem> inHandItems = new ArrayList<>();
	
	public OnHandAgent(Framework framework, Long id) {
		super(framework, id);
	}
	
	/** agent is picking up a Item */
	public void pick(Item item, int quantity) {
		for(InHandItem inHandItem : inHandItems) {
			if (inHandItem.getItem().equals(item)) {
				inHandItem.incQuantity(quantity);
				return;
			}
		}
		
		inHandItems.add(new InHandItem(item, quantity));
	}
	
	/** agent is dropping off an Item */
	public void drop(Item item, int quantity) {
		for(InHandItem inHandItem : inHandItems) {
			if (inHandItem.getItem().equals(item)) {
				if (inHandItem.getQuantity() >= quantity) {
					inHandItem.decQuantity(quantity);
					
					if (inHandItem.getQuantity() < 1) {
						inHandItems.remove(inHandItem);
					}
					
					return;
				}
			}
		}
		inHandItems.remove(item);
	}
	
	public void transfer(Item item, int quantity, OnHandAgent from, OnHandAgent to) {
		assert inHandItems.contains(item);
		from.drop(item, quantity);
		to.pick(item, quantity);
	}
}
