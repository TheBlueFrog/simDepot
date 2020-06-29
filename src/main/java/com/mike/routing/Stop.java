package com.mike.routing;

import com.mike.agents.InHandItem;
import com.mike.util.Location;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * a Stop is a location where we do something, could be
 * picks or drops.  We can pick from a Truck or a Supplier.
 * We can drop at a Customer or Truck
 *
 * Note that what we do is actually a list of actions
 * we could pick from a Truck and drop at the Truck
 * or there can be multiple drops or picks and it can
 * be a mix
 */
public class Stop extends Location {

    static private long nextId = 1;

    private long id = nextId++;
    public long getId() {
        return id;
    }

    public Location getLocation() {
        return this;
    }
	
	public List<Pair<Action, InHandItem>> getToDo() {
    	return stuffToDo;
	}
	
	static public enum Action {
        Drop,
        Pick
    };
    
    List<Pair<Action, InHandItem>> stuffToDo = new ArrayList<>();
    
    @Override
    public String toString() {
        return String.format("{ Location: id : %d}", getId());
    }

    public Stop(Location location, List<Pair<Action, InHandItem>> stuffToDo) {
        super(location);

        // TODO validate input, check for overlap?
		this.stuffToDo.addAll(new ArrayList<>(stuffToDo));
    }

}
