package com.mike.sim;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

public class Message
{
	public Agent mSender = null;
	public Class<? extends Agent> mRecipient = null;
	public long serialNumber;
	public Object mMessage;
	
//	public Message (Agent sender, Agent recipient, String msg)
//	{
//		mSender = sender;
//		mRecipient = recipient;
//		mMessage = msg;
//	}
//
//	public Message(String msg)
//	{
//		mMessage = msg;
//	}

	public Message(Agent sender, Class<? extends Agent> class1, long serialNumber, Object msg)
	{
		mSender = sender;
		mRecipient = class1;
		this.serialNumber = serialNumber;
		mMessage = msg;
	}

	public Message(Message m) {
		this.mSender = m.mSender;
		this.mRecipient = m.mRecipient;
		this.serialNumber = m.serialNumber;
		this.mMessage = m.mMessage;
	}
}
