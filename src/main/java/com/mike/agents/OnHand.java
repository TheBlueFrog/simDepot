package com.mike.agents;

import com.mike.sim.Agent;

public interface OnHand {
	
	public abstract void add(Item item);
	public abstract void delete(Item item);
	
	public abstract void transfer(Agent from, Agent to);
}
