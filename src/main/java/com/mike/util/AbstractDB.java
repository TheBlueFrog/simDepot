package com.mike.util;

import java.io.File;
import java.sql.*;
import java.util.List;

/**
 * Created by mike on 8/6/2016.
 */
abstract public class AbstractDB {
    protected String TAG;
    protected String mdbfname = "main.db";
    protected Connection mDB;

    public Connection getConnection () {
        return mDB;
    }

    public void close() throws SQLException {
        mDB.close();
    }

    /**
     * interface to let common code move DB data into an object
     */
    public interface DBtoField {
        /**
         * implementers will be called when a ResultSet is available
         * and asked to update the corresponding in-memory object
         *
         * @param rs
         * @throws SQLException
         */
        public void dbToField(ResultSet rs) throws SQLException;
    }
    /**
     * interface to construct a new object from data in the DB
     */
    public interface constructfromDB {
        /**
         *
         * @param rs
         * @throws SQLException
         */
        public Object construct(ResultSet rs) throws SQLException;
    }


    protected AbstractDB(String dbfname, String tag) throws ClassNotFoundException, SQLException {

        TAG = tag;
        Class.forName("org.sqlite.JDBC");

        Log.d(TAG, "Use db " + new File(dbfname).getAbsolutePath());

        mdbfname = dbfname;
        try {
            mDB = DriverManager.getConnection("jdbc:sqlite:" + mdbfname);
        }
        catch (SQLException e) {
            mDB = null;
            throw new SQLException(e);
        }

//        mDB.setAutoCommit(false);
    }

    public void commit () throws SQLException {
//        mDB.commit(); //db is in auto-commit mode?
    }

    public void cleanup(PreparedStatement s) {
        if (s != null)
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
    }

    public long insert(Connection db, String q) throws SQLException {
        PreparedStatement s = null;
        try {
            s = db.prepareStatement(q);
            s.execute();
            ResultSet rs = s.getGeneratedKeys();
            long rowID = rs.getInt("last_insert_rowid()");
            s.close();
            return rowID;

//            updateRowID (db, );
        } finally {
            cleanup(s);
        }
    }

    public void insert(Connection db, String q, DBtoField dbToField) throws SQLException {
        PreparedStatement s = null;
        try {
            s = db.prepareStatement(q);
            s.executeUpdate();
            s.close();
        } finally {
            cleanup(s);
        }
    }

    public void update(Object tableName, List<String> colNames, List<String> values) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("update %s set ", tableName);
            for (int i = 0; i < colNames.size(); /* */) {
                q = q
                        + String.format("%s = %s", colNames.get(i), values.get(i));
                if (++i < colNames.size())
                    q = q + ", ";
                else
                    q = q + ";";
            }
            s = mDB.prepareStatement(q);
            s.executeUpdate();
            s.close();
        } finally {
            cleanup(s);
        }
    }

    /**
     *
     * @param db
     * @param q query string to fetch a set of objects
     * @param f function to call to update each object
     * @return crap @TODO fix
     * @throws SQLException
     */
     public boolean read (Connection db, String q, constructfromDB f) throws SQLException {
        PreparedStatement s = null;
        try {
            s = db.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                f.construct(rs);
                return true;
            }
        } finally {
            cleanup(s);
        }

        throw new IllegalStateException(
                String.format("Failed query: %s", q));
//        return false;
    }

    /**
     * read the record with the highest auto-increase value, id
     *
     * @param db
     * @return
     * @throws SQLException
     */
    public boolean readLast(Connection db, String tableName, constructfromDB c) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("select id, tick from %s order by id DESC limit 1",
                    tableName);
            s = db.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                c.construct(rs);
                return true;
            }
        } finally {
            cleanup(s);
        }

        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
//        return false;
    }

    public boolean tableHasRow(Connection db, String tableName, long id) throws SQLException {
        PreparedStatement s = null;
        try {
            String q = String.format("select id from %s",
                    tableName);
            s = db.prepareStatement(q);
            ResultSet rs = s.executeQuery();
            return rs.next();
        } finally {
            cleanup(s);
        }

//        throw new IllegalStateException(String.format("Failed to read last record from table %s", tableName));
    }
}
