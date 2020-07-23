import com.mongodb.*;

import java.net.UnknownHostException;

public class DatabaseConnectivity
{
	private static MongoClient mongoClient;

	static
	{
		try
		{
			mongoClient = new MongoClient();
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}

	private static DB database = mongoClient.getDB("vaple");
	public static final DBCollection ACCOUNTCOLLECTION = database.getCollection("account");
	public static final DBCollection ACTIVITYCOLLECTION = database.getCollection("activity");

	public static void addObject(DBObject object, DBCollection collection)
	{
		collection.insert(object);
	}

	public static DBObject findObject(DBObject object, DBCollection collection)
	{
		return collection.findOne(object);

	}

	public static void updateObject(DBObject object, DBCollection collection)
	{
		collection.update(new BasicDBObject("_id", object.get("id")), object);
	}


}