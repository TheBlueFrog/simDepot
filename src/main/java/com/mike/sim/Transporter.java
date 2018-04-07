package com.mike.sim;

import com.mike.util.Location;
import com.mike.util.Log;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by mike on 6/17/2016.
 */
public class Transporter extends LocatedAgent {

    static private final String TAG = Transporter.class.getSimpleName();

    public static double DollarsPerGallonFuel = 2.50;
    public static double VehicleMass = (3000.0 / 2.2046);
    public static double VehicleMileage = (10.0);
    public static double VehicleDollarPerKgM = (1.0 / (VehicleMileage * VehicleMass * 1600.0)) * DollarsPerGallonFuel;
    public static double VehicleAverageSpeedMiPHr = 3.0;
    public static double MaxLoadKg = 100.0; // maximumKg net buildWants

    // get speed in meters per second
    public double speedMPS = VehicleAverageSpeedMiPHr * Constants.MetersPerMile / (60 * 60);
    public double getSpeedMPS() {
        return speedMPS;
    }

    public static double movementCostPerKgM = VehicleDollarPerKgM;

    // when moving how much (in meters) we move per tick
    private double metersPerTick;
    private double dxMetersPerTick;
    private double dyMetersPerTick;

    private TripInfo tripInfo = null;

    @Override
    protected String getClassName() {
        return null;
    }

    /**
     * draw a circle where we are on the map
     * @param g2    graphics context to use to paint this agent
     */
    @Override
    void paint(Graphics2D g2) {

        Shape circle = null;
        if (circle == null) {
            double x = Constants.deg2PixelX(getLocation().x);
            double y = Constants.deg2PixelY(getLocation().y);

            circle = new Ellipse2D.Double(x, y, 5.0f, 5.0f);
        }

        Color c = Color.gray;
        if (tripInfo != null) {
            switch (tripInfo.getState()) {
                case NotStarted:
                    c = Color.gray;
                    break;
                case Arrived:
                    c = Color.red;
                    break;
                case Enroute:
                    c = Color.blue;
                    break;
                default:
                    c = Color.gray;
                    break;
            }
        }

        g2.setColor(c);
        g2.draw(circle);
    }

    @Override
    public String toString () {
        return String.format("Transporter %d, ",
                getSerialNumber());
    }

    public Transporter(Framework f, Long serialNumber) {
        super(f, serialNumber);

        location = new Location(
                Constants.MapLeft + ((Constants.MapRight - Constants.MapLeft) / 2.0),
                Constants.MapBottom + ((Constants.MapTop - Constants.MapBottom) / 2.0));
                // Constants.randomFarmLocation();

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        assert msg.serialNumber == this.getSerialNumber();

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
            // frameworks says everyone is ready, we're waiting
            send(new Message(this, Clock.class, 0, "subscribe"));
            send(new Message(this, Scheduler.class, 0, "available"));
            return;
        }
        if (msg.mSender instanceof Clock) {
            tick (Clock.getTime());
        }
        else if ((msg.mSender instanceof Scheduler) && (msg.mMessage instanceof TripInfo)) {
            TripInfo tripInfo = (TripInfo) msg.mMessage;
            this.tripInfo = tripInfo;
        }
        else if ((msg.mSender instanceof Transporter) && (msg.serialNumber == getSerialNumber())) {
            // msg from myself
        }
        else {
            Log.e(TAG, "Unhandled message ignored.");
        }
    }

    private Map<Consumer,List<Order>> listToMap(List<Order> orders) {
        Map<Consumer,List<Order>> x = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getConsumer()));
        return x;
    }

    private void tick(long curTime) {
        if (tripInfo == null)
            return;

        switch (tripInfo.getState()) {
            case NotStarted:
                tripInfo.start();
                break;
            case Enroute:
                tripInfo.move();
                break;
            case Arrived:
                tripInfo.arrived();
                break;
        }

        Main.repaint();
    }

    public Location getHome() {
        return new Location(getLocation());
    }

    public String getLabel() {
        return String.format("Transporter %d", getSerialNumber());
    }

    /** send TripInfo back to scheduler and then tell him we're available and ready to go */
    public void tripCompleted() {
        send(new Message(this, Scheduler.class, 0, tripInfo));
        tripInfo = null;

        send(new Message(this, Scheduler.class, 0, "available"));
    }
}
