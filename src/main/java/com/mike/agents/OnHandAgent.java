package com.mike.agents;

import com.mike.market.Item;
import com.mike.sim.Framework;
import com.mike.sim.LocatedAgent;

import java.util.ArrayList;
import java.util.List;

abstract public class OnHandAgent extends LocatedAgent {
	
	protected List<Item> onHand = new ArrayList<>();
	
	public OnHandAgent(Framework framework, Long id) {
		super(framework, id);
	}
	
	public void pick(Item item) {
		assert ! onHand.contains(item);
		onHand.add(item);
	}
	public void drop(Item item) {
		assert onHand.contains(item);
		onHand.remove(item);
	}
	
	public void delete(Item item) {
		if ( ! onHand.remove(item))
			assert false;
	}
	
	public void transfer(Item item, OnHandAgent from, OnHandAgent to) {
		assert onHand.contains(item);
		from.drop(item);
		to.pick(item);
	}
}
