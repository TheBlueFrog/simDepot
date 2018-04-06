package com.mike.sim;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mike on 6/27/2016.
 */
public class TransporterBid {

    private static long sBidTag = 0;

    public static long nextBidTag() {
        return sBidTag++;
    }

    private final long tag; // identifies a set of bids
    private boolean rejected = false;

    private List<Order> orders;
    private Transporter transporter;
    private double bid = Double.POSITIVE_INFINITY;
    private boolean awarded = false;

    public TransporterBid(long tag, List<Order> orders, Transporter transporter) {
        this.tag = tag;
        this.orders = orders;
        this.transporter = transporter;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public long getTag() {
        return tag;
    }

    public String getTimeStampAsString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyy.mm.dd HH:mm.ssss z");
        return df.format(new Date(tag));
    }

    public double getBid() {
        return bid;
    }
    public Transporter getTransporter() {
        return transporter;
    }

    public boolean getAwarded() {
        return awarded;
    }
    public void setBid(double bid) { this.bid = bid; }

    public void setAwarded(boolean awarded) {
        this.awarded = awarded;
        this.rejected = false; // reset this too
    }

    public void rejectAwardedBid() { this.rejected = true; }
    public boolean rejected() { return rejected; }
}
