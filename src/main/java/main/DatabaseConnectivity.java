package main;

import com.mongodb.*;
import databaseobject.Activity;
import databaseobject.User;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <PRE> This class implements methods to allow for modification of the database</PRE>
 */
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
	public static final DBCollection CLUBCOLLECTION = database.getCollection("club");
	public static final DBCollection CHALLENGECOLLECTION = database.getCollection("challenge");

	/**
	 * Adds an object to the database
	 * @param object The object to be added
	 * @param collection The collection which will store the object
	 */
	public static void addObject(DBObject object, DBCollection collection)
	{
		collection.insert(object);
	}

	/**
	 * Finds one object from a collection
	 * @param object The object to be found
	 * @param collection The collection to search from
	 * @return Returns the desired object
	 */
	public static DBObject findOneObject(DBObject object, DBCollection collection)
	{
		return collection.findOne(object);
	}

	/**
	 * Returns a list of all objects that share a specified common property
	 * @param varName The property name
	 * @param data The value of the property
	 * @param collection The collection to search through
	 * @return The list of all objects that share that common property
	 */
	public static List<DBObject> findObjectsByCommonProperty(String varName, String data, DBCollection collection)
	{
		return collection.find(new BasicDBObject(varName,data)).toArray();
	}

	/**
	 * Updates a specificed object
	 * @param object The updated object
	 * @param collection The collection in which the object is in
	 */
	public static void updateObject(DBObject object, DBCollection collection)
	{
		collection.update(new BasicDBObject("_id", object.get("_id")), object);
	}

	public static void removeObject(DBObject object, DBCollection collection)
	{
		collection.remove(object);
	}

	/**
	 *
	 * @param object The object to be searched for
	 * @param collection The collection to search in
	 * @return Returns true if the object exists in the specified collection, false otherwise
	 */
	public static boolean doesObjectExist(DBObject object, DBCollection collection)
	{
		return findOneObject(object, collection) != null;
	}

	public static List<DBObject> getAllObjectsByProperty(String data, DBCollection collection)
	{
		List<DBObject> finalList = new ArrayList<DBObject>();

		ArrayList<DBObject> objects = (ArrayList<DBObject>)collection.find().toArray();
		for (int i = 0; i < objects.size(); i++)
		{
				ArrayList<String> stringys = new ArrayList<String>(objects.get(i).keySet());
				for (int j = 0; j < stringys.size(); j++)
				{
					if (objects.get(i).get(stringys.get(j)).equals(data))
					{
						finalList.add(objects.get(i));
						break;
					}
				}
		}

		return finalList;
	}

	public static void main(String[] args)
	{
		User user = new User();
		User user2 = (User)user.getFromInfoInDataBase("email","gay@gmail.com");
		ArrayList<ObjectId> list = user2.mutualFriends(user2.getObjectID());
		System.out.println(list);
	}

}