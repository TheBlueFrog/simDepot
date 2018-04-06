package com.mike.sim;

import com.mike.util.Location;
import com.mike.util.Log;
import com.mike.util.MyException;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by mike on 6/17/2016.
 */
public class Supplier extends LocatedAgent {

    static private final String TAG = Supplier.class.getSimpleName();

    static private List<Supplier> suppliers = new ArrayList<>();
    static private List<Item> items = new ArrayList<>();

    public static Supplier pickSupplier(Consumer consumer) {
        return suppliers.get(Main.getRandom().nextInt(suppliers.size()));
    }

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

            s = new RoundRectangle2D.Double(x, y, 7, 7, 2, 2);
        }

        Color c = Color.green;
//        if (outOfStock())
//            c = Color.red;

//        c = Color.BLUE;
//        if (getSerialNumber() == 1)
//            c = Color.MAGENTA;
//        if (getSerialNumber() == 2)
//            c = Color.GREEN;

        g2.setColor(c);
        g2.draw(s);

    }

    @Override
    public String toString () {
        return String.format("Supplier %d", getSerialNumber());
    }

    public Item pickItem(Consumer consumer) {
        return items.get(Main.getRandom().nextInt(items.size()));
    }

    public Supplier(Framework f, final Long id) throws MyException, SQLException {
        super(f, id);
        suppliers.add(this);

        switch (Main.getScenario()) {
            case 0: {
                // put suppliers on the left, consumers on the right
                double dx = id * (Location.MapWidthDeg / 10.0);
                Location loc = new Location(Location.MapCenter.x - dx, Location.MapCenter.y);
                setLocation(loc);

                items.add(new Item(this, "one"));
                items.add(new Item(this, "two"));
                items.add(new Item(this, "three"));
            }
            break;

            default:
                assert false;
                break;
        }

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {

            send(new Message(this, Clock.class, 0, "subscribe"));

            // frameworks says everyone is ready, start
        }
        else if (msg.mSender instanceof Clock) {
            if (Clock.getTimeOfDayInMinutes() < 1) {
                endOfDay(Clock.getDayOfMonth());
            }
        }
    }

    private void endOfDay(long dayOfMonth) {
    }


    public boolean pickup(Transporter t, Order item) {

        synchronized (this) {
            boolean r = false;
            String fs;

                fs = "%4d load %4d on Transporter %4d for Consumer %4d";
                r = true;

            Log.d(TAG, String.format(fs,
                    getSerialNumber(),
                    item.getID(),
                    t.getSerialNumber(),
                    item.getConsumer().getSerialNumber()));
            return r;
        }
    }

}
