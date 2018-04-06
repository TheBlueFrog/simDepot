package com.mike.sim;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

import com.mike.sim.Agent;
import com.mike.sim.Message;
import com.mike.util.AbstractDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mike on 6/7/2016.
 */
public class DB { //extends AbstractDB {

    /**
     * interface to construct a new object from data in the DB
     */
    public interface constructfromRecordSet {
        /**
         *
         * @param rs
         * @throws SQLException
         */
        public void construct(Agent agent, ResultSet rs) throws SQLException;
    }

    private String dbname = null;
    public DB(String dbname) { //throws SQLException, ClassNotFoundException {
        this.dbname = dbname;
//        super (dbname, DB.class.getSimpleName());


//        mDB.setAutoCommit(false);
    }

    public boolean insertTransport(Agent agent,
                                   long transporterSN,
                                   int itemsDelivered,
                                   TripInfo ti,
                                   double tripCost) throws SQLException {
        return true;
    }
    /*
        long now = Clock.getTime();
        String q = String.format(
                "insert into Transporter values (null, "
                        + "\"%d\", \"%d\", \"%d\", \"%d\", \"%d\", \"%d\", \"%.2f\", \"%d\", \"%.2f\", \"%2.2f\");",
                now,
                Clock.getDay(now),
                Clock.getHourOfDay(now),
                Clock.getMinuteOfHour(now),
                transporterSN,
                itemsDelivered,
                ti.getTripLength() / 1000.0,
                ti.getTripTime(),
                tripCost,
                Clock.getTimeOfDayDouble(Clock.getTimeOfDay(ti.getTripStartTime())));

        agent.send(new Message(agent, DBAgent.class, 0, q));
        return true;

//        PreparedStatement s = null;
//        try {
//
//            s = mDB.prepareStatement(q);
//            boolean b = s.execute();
//            commit();
//            return true; // always false? b && b1;
//        } finally {
//            cleanup(s);
//        }
    }

    public boolean insertStock(Agent agent,
                               int supplierSN,
                               AnimalSupplier.AnimalStockParams beef,
                               AnimalSupplier.AnimalStockParams pork,
                               AnimalSupplier.AnimalStockParams lamb) throws SQLException {

        long now = Clock.getTime();
        String q = String.format(
                "insert into AnimalStock values (null, "
                        + "\"%d\", \"%d\", \"%d\", "
                        + "\"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", "
                        + "\"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", "
                        + "\"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\""
                        + ");",
                now,
                Clock.getDay(now),
                supplierSN,

                beef != null ? beef.getHerdSize() : 0.0,
                0.0,
                beef != null ? beef.monthlyDemandKg : 0.0,
                beef != null ? beef.onHandKg : 0.0,
                beef != null ? beef.loadedTodayKg : 0.0,
                beef != null ? beef.failedLoadKg : 0.0,

                pork != null ? pork.getHerdSize() : 0.0,
                0.0,
                pork != null ? pork.monthlyDemandKg : 0.0,
                pork != null ? pork.onHandKg : 0.0,
                pork != null ? pork.loadedTodayKg : 0.0,
                pork != null ? pork.failedLoadKg : 0.0,

                lamb != null ? lamb.getHerdSize() : 0.0,
                0.0,
                lamb != null ? lamb.monthlyDemandKg : 0.0,
                lamb != null ? lamb.onHandKg : 0.0,
                lamb != null ? lamb.loadedTodayKg : 0.0,
                lamb != null ? lamb.failedLoadKg : 0.0
        );

        agent.send(new Message(agent, DBAgent.class, 0, q));

////        PreparedStatement s = null;
//        Statement st1 = mDB.createStatement();
//
//        try {
//            st1.executeUpdate(q);
////            mDB.commit();
////            mDB.setAutoCommit(true);
//            return true; // always false? b && b1;
//        } finally {
////            cleanup(s);
//            st1.close();
//        }

        return true;
    }

    public void insertStock(Agent agent,
                            int supplierSN,
                            ProduceSupplier.ProduceStockParams p) {

            long now = Clock.getTime();
            String q = String.format(
                    "insert into ProduceStock values (null, "
                            + "\"%d\", \"%d\", \"%d\", \"%d\", "
                            + "\"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\", \"%.2f\""
                            + ");",
                    now,
                    Clock.getDay(now),
                    supplierSN,
                    p.getStockID(),

                    p.acreage,
                    p.monthlyDemandKg,
                    p.onHandKg,
                    p.loadedTodayKg,
                    p.failedLoadKg);

            agent.send(new Message(agent, DBAgent.class, 0, q));
        }
*/
    public void loadSupplier(Agent agent, String table, constructfromRecordSet c) throws SQLException {
/*
        PreparedStatement s = null;
        try {
            String q = String.format("select * from %s where (id = %s)", table, agent.getSerialNumber());
            s = mDB.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                c.construct(agent, rs);
            }
        } finally {
            cleanup(s);
        }

//        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
*/
    }

/*
    public void loadSupplierStock(Agent agent, String table, constructfromRecordSet c) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("select * from %s where (Supplier = %s)", table, agent.getSerialNumber());
            s = mDB.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                c.construct(agent, rs);
            }
        } finally {
            cleanup(s);
        }

//        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
    }
*/
    public void loadConsumer(Agent agent, String table, constructfromRecordSet c) throws SQLException {
/*
        PreparedStatement s = null;
        try {
            String q = String.format("select * from %s where (id = %s)", table, agent.getSerialNumber());
            s = mDB.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                c.construct(agent, rs);
            }
        } finally {
            cleanup(s);
        }

//        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
*/
    }
    /*
    public void loadConsumerParams(Agent agent,  String table, constructfromRecordSet c) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("select * from %s where (Consumer = %s)", table, agent.getSerialNumber());
            s = mDB.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                c.construct(agent, rs);
            }
        } finally {
            cleanup(s);
        }

//        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
    }

    public String getItemDescription(long itemID) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("select * from Consumable where (id = %s)", itemID);
            s = mDB.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return rs.getString(2);
            }
        } finally {
            cleanup(s);
        }
        assert false;
        return null;
    }

    */
}
