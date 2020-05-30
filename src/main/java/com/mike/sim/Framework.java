package com.mike.sim;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

import com.mike.util.Log;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class Framework
{
	private static final String TAG = Framework.class.getSimpleName();

	static public enum State { Start, AgentsRegistering, AgentsRunning };

	private State state = State.Start;

	public State getState () { return this.state; }


	private List<AgentInfo> agentInfo = null;

	public Framework(List<AgentInfo> agents) {

		// setup the agents
		try
		{
			agentInfo = agents;

			state = State.AgentsRegistering;
			setupAgents();
		}
		catch(IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException| NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	}

	private void setupAgents() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		for (AgentInfo x : agentInfo) {

			Constructor<? extends Agent> c = x.agentClass.getConstructor(Framework.class, Long.class);
			for (long i = 0; i < x.copies; ++i) {
				x.state = 1;
				Agent a = c.newInstance(this, i);
				a.start();
			}
		}
	}

	/**
	 * agents will notify us when they are ready, when all agents
	 * are ready we notify all agents
	 *
	 */
	public void register(Agent agent) {
		for (AgentInfo x : agentInfo) {
			if (x.agentClass == agent.getClass()) {
				x.agents.put(agent.getSerialNumber(), agent);

				if (x.agents.size() == x.copies)
					x.state = 2;
			}
		}

		// when -all- agents have registered we transition to the Running state
		// and notify them

		boolean all = true;
		for (AgentInfo x : agentInfo) {
			if (x.state < 2)
				all = false;
		}

		if (all) {
			Log.d(TAG, "All agents registered");
			state = State.AgentsRunning;

			for (AgentInfo x : agentInfo) {
				for (int i = 0; i < x.copies; ++i) {
					Message m = new Message(null, x.agentClass, i, state);
					x.state = 3;
					send(m);
				}
			}

			assert state == State.AgentsRunning;
		}
	}

	/**
	 * send message to recipient
	 * @param m
	 */
	public boolean send(Message m)
	{
		if (m.mRecipient == null)
			throw new IllegalStateException("Message has no recipient");

		List<Agent> r = getRecipients(m.mRecipient, m.serialNumber);
		if (r.size() > 0) {
			for (Agent a : r)
				a.incoming(m);
		} else {
			Log.e(TAG, String.format("%s is not a known agent", m.mRecipient.getName()));
			return false;
		}

		return true;
	}

//	public void broadcast(Message message)
//	{
//		for (Agent a : r)
//			a.incoming(message);
//	}

	/**
	 * assemble a list of agents to send the message to,
	 *
	 * @param class1		either leaf class or interior class in the agent hierarchy, if
	 *                      interior all derived classes match
	 * @param serialNumber	either -1 or explicit sn, -1 matches all sn, there is code
	 *                      in the dispatching of the message to patch the sn back to what
	 *                      it should be
     * @return
     */
	private List<Agent> getRecipients(Class<? extends Agent> class1, long serialNumber)
	{
		List<Agent> v = new ArrayList<Agent>();
		for (AgentInfo a : agentInfo)
		{
			if (class1.isAssignableFrom(a.agentClass)) {
				if (serialNumber == -1) {
					for (Long i : a.agents.keySet())
						v.add(a.agents.get(i));
				}
				else {
					Agent agent = a.agents.get(serialNumber);
					if (agent.getSerialNumber() == serialNumber)
						v.add(agent);
				}
			}
		}
		
		return v;
	}

	static public interface agentWalker {
		public void f(Agent a);
	}
	public void walk (agentWalker f) {
		for (AgentInfo ai : agentInfo) {
			for (long i = 0; i < ai.copies; ++i) {
				f.f(ai.agents.get(i));
			}
		}
	}
}
