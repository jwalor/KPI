package org.apache.storm.mongodb.common;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;

/**
 * 
 * @author jalor
 *
 */
public class MongoDBClient {

    private MongoClient client;
    private MongoCollection<Document> collection;
    private String database;

    public MongoDBClient(String url, String collectionName) {
        //Creates a MongoURI from the given string.
        MongoClientURI uri = new MongoClientURI(url);
        //Creates a MongoClient described by a URI.
        this.client = new MongoClient(uri);
        //Gets a Database.
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        //Gets a collection.
        this.collection = db.getCollection(collectionName);
    }
   
    public MongoDBClient(String userName, String password,  String database,  final String host, final int port) {
        
    	if ( StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
    		
    		this.client = new MongoClient(new ServerAddress(host, port));
    	}else {
    		
    		MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
    		this.client = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
    	}
    	
    	this.database = database;
    }
    
    public MongoCollection<Document> getCollecion(String collectionName ){
    	
    	MongoDatabase db = client.getDatabase(database);
    	if (this.client != null){
    		return db.getCollection(collectionName);
    	}else
    		return  null;
    }
    
    /**
     * Inserts one or more documents.
     * This method is equivalent to a call to the bulkWrite method.
     * The documents will be inserted in the order provided, 
     * stopping on the first failed insertion. 
     * 
     * @param documents
     */
    public void insert(List<Document> documents, boolean ordered) {
        InsertManyOptions options = new InsertManyOptions();
        if (!ordered) {
            options.ordered(false);
        }
        collection.insertMany(documents, options);
    }
    
    
    /**
     * Insert one document.
     * This method is equivalent to a call to the bulkWrite method.
     * The document will be inserted in the order provided, 
     * stopping on the first failed insertion. 
     * 
     * @param documents
     */
    public void insert(Document document , String collectionName) {
    	collection = getCollecion(collectionName);
        collection.insertOne(document);
    }
    
    /**
     * Update a single or all documents in the collection according to the specified arguments.
     * When upsert set to true, the new document will be inserted if there are no matches to the query filter.
     * 
     * @param filter
     * @param document
     * @param upsert a new document should be inserted if there are no matches to the query filter
     * @param many whether find all documents according to the query filter
     */
    public void update(Bson filter, Bson document, boolean upsert, boolean many) {
        //TODO batch updating
        UpdateOptions options = new UpdateOptions();
        if (upsert) {
            options.upsert(true);
        }
        if (many) {
            collection.updateMany(filter, document, options);
        }else {
            collection.replaceOne(filter,  (Document) document, options);
          //  collection.updateOne(filter, document);
        }
    }

    /**
     * Finds a single document in the collection according to the specified arguments.
     *
     * @param filter
     */
    public Document find(Bson filter) {
        //TODO batch finding
    	
        return collection.find(filter).first();
    }
    
    
    public Document find(Bson filter , String collectionName ) {
        //TODO batch finding
    	collection = getCollecion(collectionName);
        return collection.find(filter).first();
    }
    /**
     * Closes all resources associated with this instance.
     */
    public void close(){
        client.close();
    }

	public MongoCollection<Document> getCollection() {
		return collection;
	}

	public void setCollection(MongoCollection<Document> collection) {
		this.collection = collection;
	}
    
    public void delete(Bson filter , String collectionName ) {
	    collection = getCollecion(collectionName);
        collection.deleteMany(filter);
    }
    
}
