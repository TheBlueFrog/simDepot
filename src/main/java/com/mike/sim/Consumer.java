package com.mike.sim;

import com.mike.util.Location;
import com.mike.util.Log;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by mike on 6/17/2016.
 */
public class Consumer extends LocatedAgent {

    static private final String TAG = Consumer.class.getSimpleName();

    private List<Order> waitingFor = new ArrayList<>();
    private long lastDay;

    @Override
    protected String getClassName() {
        return null;
    }

    @Override
    void paint(Graphics2D g2) {

        Shape s = null;
        if (s == null) {
            double x = Constants.deg2PixelX(getLocation().x);
            double y = Constants.deg2PixelY(getLocation().y);

            s = new Rectangle2D.Double(x, y, 5.0f, 5.0f);
        }

        Color c = Color.BLUE;

//        c = Color.BLUE;
//        if (getSerialNumber() == 1)
//            c = Color.MAGENTA;
//        if (getSerialNumber() == 2)
//            c = Color.GREEN;

//        g2.setColor(c);
//        g2.draw(s);

        c = Color.GREEN;
        if (waitingFor.size() > 2)
            c = new Color(1.0f, 0f, 0f);
        else if (waitingFor.size() > 1)
            c = new Color(0.6f, 0f, 0f);

        if (c != null) {
            g2.setColor(c);
            g2.fill(s);
        }
    }

    public Consumer(Framework f, final Long serialNumber) throws SQLException {
        super(f, serialNumber);

        switch (Main.getScenario()) {
            case 0: {
                double dx = serialNumber * (Location.MapWidthDeg / 10.0);
                Location loc = new Location(Location.MapCenter.x + dx, Location.MapCenter.y);
                setLocation(loc);
            }
            break;

            default:
                assert false;
                break;
        }


//        Main.getDB().loadConsumer(this, "ConsumerConfig", new DB.constructfromRecordSet() {
//            @Override
//            public void construct(Agent agent, ResultSet rs) throws SQLException {
//                assert serialNumber == (int) rs.getLong(1);
////              INSERT INTO "ConsumerConfig" VALUES (8, -122.6298797, 45.5045639);
//                setLocation(new Location(rs.getDouble(2), rs.getDouble(3)));
//            }
//        });

//        Main.getDB().loadConsumerParams (this, "ConsumerParams", new DB.constructfromRecordSet() {
//            @Override
//            public void construct(Agent agent, ResultSet rs) throws SQLException {
//                assert agent.getSerialNumber() == (int) rs.getLong(2);
//                /*
//                CREATE TABLE ConsumerParams (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                    Consumer INTEGER);
//                    ConsumableID INTEGER,
//                    Quantity DOUBLE,
//                    Frequency DOUBLE
//                    );
//                 */
//                wants.add(new ConsumerWant(
//                        (10 * 60), // 10am
//                        1, // monday
//                        rs.getLong(3),
//                        rs.getDouble(4),
//                        (int) rs.getDouble(4)));
//            };
//        });

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
            // frameworks says everyone is ready, start

            send(new Message(this, Clock.class, 0, "subscribe"));
        }
        else if (msg.mSender instanceof Clock) {
//                for (ConsumerWant w : wants)
//                    w.setOrdered(false);

//                jiggleWants ();

//            lastDay = Clock.getDayOfWeek();

            maybeOrder ();
        }
        else if (msg.mMessage instanceof Pair) {
            Pair<String, Order> p = (Pair<String, Order>) msg.mMessage;
            if (p.getKey().equals("pickupFailure")) {
                // not doing anythig now
//                Consumable consumable = p.getValue();

//                Log.d(TAG, String.format("%3d failed to receive %.1fkg of %s",
//                        getSerialNumber(),
//                        consumable.getQuantity(),
//                        consumable.getDescription()));

                // don't do this, let the Supplier adjust itself, it knows
                // a pickup failed

//                send(new Message(this, Supplier.class, -1,
//                        new Pair<String, String>(consumable.getDescription(), "more")));
            }
        }
    }

    private void jiggleWants() {
//        for (ConsumerWant w : wants) {
//            // tod will drift a bit because of truncation...fraction of a minute per week...
//            w.tod = w.tod + ((int) (Constants.random.nextGaussian() * (30 * 60)));
//            w.quantity = w.quantity + ((int) (Constants.random.nextGaussian() * 0.1));
//
//            w.quantity = Math.min(0.2, w.quantity);
//        }
    }

    private void maybeOrder() {
        if (waitingFor.size() > 5)
            return;

        List<Order> items = new ArrayList<>();

        Order order = Main.generateOrder(this);
        items.add(order);

        waitingFor.add(order);

        if (items.size() > 0)
            send(new Message(this, Scheduler.class, 0, items));
    }

    public boolean deliver(Transporter t, Order item) {
        assert waitingFor.contains(item);

        for (Order i : waitingFor) {
            if (i.getID().equals(item.getID())) {
                waitingFor.remove(i);

                Log.d(TAG, String.format("%3d received %.1fkg of %s from Transporter %d",
                        getID(),
                        i.getID(),
                        t.getSerialNumber()));

//                send(new Message(this, Consumer.class, getSerialNumber(), "want"));
                return true;
            }
        }

        Log.d(TAG, String.format("%3d not waiting for %s",
                getSerialNumber(),
                item.getID()));
        return false;
    }
}
