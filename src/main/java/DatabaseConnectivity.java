import com.mongodb.*;
import org.bson.types.ObjectId;

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



	public static Activity findActivity(DBObject obj){
		DBObject object = ACTIVITYCOLLECTION.findOne(obj);

		if(object != null)
			return new Activity(object);
		return null;
	}

	public static Activity getActivity(ObjectId ID)
	{
		return findActivity(new BasicDBObject("_id",ID));
	}

	public static void addNewActivity(Activity activity)
	{
		ACTIVITYCOLLECTION.insert(activity.getDBform());
	}

	public static void updateActivity(Activity activity)
	{
		ACTIVITYCOLLECTION.update(new BasicDBObject("_id",activity.getObjectID()),activity.getDBform());
	}
}