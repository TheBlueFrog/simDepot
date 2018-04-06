package com.mike.sim;

import com.mike.sim.Agent;
import com.mike.sim.Framework;
import com.mike.sim.Message;
import com.mike.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mike on 8/21/2016.
 *
 * this class basically runs DB commands asych for people
 */
public class DBAgent extends Agent {

    private static final String TAG = DBAgent.class.getSimpleName();

//    private final Connection db;
//    private Statement statement;

    @Override
    protected String getClassName() { return DBAgent.class.getSimpleName(); }

    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public DBAgent(Framework f, Long serialNumber) {
        super(f, serialNumber);

//        db = Main.getDB().getConnection();
//        try {
//            statement = db.createStatement();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        register();
    }

    @Override
    protected void onMessage(Message msg) {
        if (getQueueLength() > 10)
            Log.d(TAG, String.format("Queue length %d", getQueueLength()));
        if (msg.mMessage instanceof String) {
            String s = (String) msg.mMessage;

//            try {
//                statement.executeUpdate(s);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    statement.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
