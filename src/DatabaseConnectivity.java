import com.mongodb.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Array;
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

	public static User getUserByEmail(String email)
    {
        BasicDBObject object = (BasicDBObject)accountCollection.findOne(new BasicDBObject("email",email));

		if(object == null)
			return null;
        return new User(object);
    }

	public static User getUserByToken(String token)
    {
        BasicDBObject object = (BasicDBObject)accountCollection.findOne(new BasicDBObject("token",token));

		if(object == null)
			return null;
        return new User(object);
    }

    public static boolean emailAlreadyExists(String email)
    {
        return getUserByEmail(email) != null;
    }

    public static void updateUser(User user)
    {
        accountCollection.update(new BasicDBObject("_id",user.getObjectID()),new BasicDBObject("$set",user.getDBForm()));
    }

    public static User getUser(ObjectId id)
    {
        BasicDBObject user = accountCollection.findOne(new BasicDBObject("_id", id));

		if(user == null)
			return null;
        return new User(user);
    }

	public static Activity getActivity(ObjectId ID)
    {
        BasicDBObject object = new BasicDBObject("_id",ID);

		if(object == null)
			return null;
        return new Activity(object);
    }

    public static void addNewActivity(Activity activity)
    {
        activityCollection.insert(activity.getDBform());
    }

    public static void updateActivity(Activity activity)
    {
        activityCollection.update(new BasicDBObject("_id",activity.getObjectID()),new BasicDBObject("$set",activity.getDBform()));
    }
}