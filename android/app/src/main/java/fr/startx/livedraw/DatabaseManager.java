package fr.startx.livedraw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;

public class DatabaseManager {
    private static class SingletonHolder {
        public final static DatabaseManager instance = new DatabaseManager();
    }

    public static final String DB_NAME = "livedraw";
    public static final String TAG = "livedraw";
    private static final String syncGatewayIP = "ws://15.188.11.132";
    private static final String syncGatewayPort = "4984";

    private Database database;
    private Replicator replicator;

    private DatabaseManager() {
    }

    public void init(Context context) {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        try {
            database = new Database(DB_NAME, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG,"Error getting database");
        }

        replicator = createReplicator(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        replicator.addChangeListener(new ReplicatorChangeListener() {
            @Override
            public void changed(@NonNull ReplicatorChange change) {
                Log.w(TAG, "Replicator status was changed:" + change.getStatus());
            }
        });
        replicator.start();
    }

    public boolean saveDocument(MutableDocument doc) {
        try {
            database.save(doc);
            Log.w(DatabaseManager.TAG, "Document was saved");
            return true;
        } catch (CouchbaseLiteException e) {
            Log.e(DatabaseManager.TAG, "Error saving document", e);
            return false;
        }
    }

    // ----- Private methods ----------

    private Replicator createReplicator(ReplicatorConfiguration.ReplicatorType rType) {
        ReplicatorConfiguration config;

        try {
            URI uri = new URI(syncGatewayIP + ":" + syncGatewayPort + "/" + DB_NAME);

            config = new ReplicatorConfiguration(database, new URLEndpoint(uri));
            config.setReplicatorType(rType);
            config.setContinuous(true);
        } catch (Exception e) {
            Log.e(TAG, "Error creating " + rType + "configuration", e);
            return null;
        }

        return new Replicator(config);
    }

    // ----- Static methods -----------

    /**
     * Create a document to the Couchbase Lite Database.
     *
     * @param  doc  a Couchbase Lite document
     * @return      <code>true</code> if the document is successfully created.
     * @see         com.couchbase.lite.MutableDocument
     */
    public static boolean createDocument(MutableDocument doc) {
        return getInstance().saveDocument(doc);
    }

    /**
     * Remove a document to the Couchbase Lite database.
     *
     * @param  doc  a Couchbase Lite document
     * @return      <code>true</code> if the document is successfully removed.
     * @see         com.couchbase.lite.Document
     * @see         com.couchbase.lite.MutableDocument
     */
    public static boolean deleteDocument(Document doc) {

        // TODO: Add check to verify if the document exists in CBL
        try {
            getInstance().database.delete(doc);
            return true;
        } catch (CouchbaseLiteException e) {
            Log.e(DatabaseManager.TAG, "Error deleting document", e);
            return false;
        }
    }

    /**
     * Remove a document to the Couchbase Lite Database.
     *
     * @param  id  a Couchbase Lite document ID
     * @return      <code>true</code> if the document is successfully removed or not exists.
     */
    public static boolean deleteDocument(String id) {
        Document doc = getInstance().database.getDocument(id);
        return doc != null ? deleteDocument(doc) : true;
    }

    /**
     * Get the Couchbase Lite database.
     *
     * @return      The instantiated Couchbase Lite database.
     */
    public static Database getDatabase() {
        return getInstance().database;
    }

    /**
     * Get the instance of this class.
     *
     * @return      The instantiated DatabaseManager class.
     */
    public static DatabaseManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Get the Couchbase Lite replicator.
     *
     * @return      The instantiated Couchbase Lite replicator.
     */
    public static Replicator getReplicator() {
        return getInstance().replicator;
    }

    /**
     * Update a document to the Couchbase Lite Database.
     *
     * @param  doc  a Couchbase Lite document
     * @return      <code>true</code> if the document is successfully updated.
     * @see         com.couchbase.lite.MutableDocument
     */
    public static boolean updateDocument(MutableDocument doc) {
        return getInstance().saveDocument(doc);
    }

}