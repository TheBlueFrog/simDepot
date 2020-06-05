package com.mike.sim;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

public class Message
{
	public Agent sender = null;
	public Class<? extends Agent> recipient = null;
	public long recipientSerialNumber;
	public Object message;
	
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

	// having a target class and serial number gives two levels
	// of addressing, all of a class (or derived) and specific member
	// of a class
	public Message(Agent sender, Class<? extends Agent> recipientClass, long recipientSerialNumber, Object msg)
	{
		this.sender = sender;
		recipient = recipientClass;
		this.recipientSerialNumber = recipientSerialNumber;
		message = msg;
	}
	public Message(Agent sender, Class<? extends Agent> recipientClass, Object msg)
	{
		this.sender = sender;
		recipient = recipientClass;
		this.recipientSerialNumber = -1; // matches all in class
		message = msg;
	}

	public Message(Agent sender, Agent recipient, Object msg) {
		this.sender = sender;
		this.recipient = recipient.getClass();
		this.recipientSerialNumber = recipient.getSerialNumber();
		this.message = msg;
	}
	
	public Message(Message m) {
		this.sender = m.sender;
		this.recipient = m.recipient;
		this.recipientSerialNumber = m.recipientSerialNumber;
		this.message = m.message;
	}
}
