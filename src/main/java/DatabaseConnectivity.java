import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;

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
	private static DBCollection accountCollection = database.getCollection("account");
	private static DBCollection activityCollection = database.getCollection("activity");

	public static void addNewUser(User user)
	{
		String email = user.getEmail();
		if (accountCollection.findOne(new BasicDBObject("email",email)) == null)
		{
			accountCollection.insert(user.getDBForm());
		}
		else
		{
			assert false;
		}
	}

	public static User findUser(BasicDBObject obj){
		DBObject object = accountCollection.findOne(obj);

		if(object != null)
			return new User(object);
		return null;
	}

	public static User getUserByEmail(String email)
	{
		return findUser(new BasicDBObject("email",email));
	}

	public static User getUserByToken(String token)
	{
		return findUser(new BasicDBObject("token",token));
	}

	public static boolean userInfoExist(String varName, String data )
	{
		return true;
	}

	public static boolean emailAlreadyExists(String email)
	{
		return getUserByEmail(email) != null;
	}

	public static void updateUser(User user)
	{
		accountCollection.update(new BasicDBObject("_id",user.getObjectID()),user.getDBForm());
	}

	public static User getUser(ObjectId id)
	{
		return findUser(new BasicDBObject("_id", id));
	}

	public static Activity findActivity(BasicDBObject obj){
		DBObject object = activityCollection.findOne(obj);

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
		activityCollection.insert(activity.getDBform());
	}

	public static void updateActivity(Activity activity)
	{
		activityCollection.update(new BasicDBObject("_id",activity.getObjectID()),activity.getDBform());
	}
}