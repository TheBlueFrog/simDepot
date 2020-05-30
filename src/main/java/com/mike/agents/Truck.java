package com.mike.agents;

import com.mike.sim.Framework;
import com.mike.sim.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * trucks act like a supplier to consumers and as a consumer to
 * suppliers.
 *
 * trucks pick items from a supplier
 *
 * trucks drops items at the consumer
 *
 * trucks continually try to compute an optimal route to
 * 		drop items on-board at the consumer
 * 		pick items that are not on-board from the supplier
 *
 *
 */
public class Truck extends Supplier {

    public Truck(Framework framework, long id) {
        super(framework, id);
    }
	
	@Override
	protected void onMessage(Message msg) {
	
	}
	
	@Override
	protected void paint(Graphics2D g2) {
	
	}
}
