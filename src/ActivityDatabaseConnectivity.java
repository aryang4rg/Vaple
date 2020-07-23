import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class ActivityDatabaseConnectivity
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
    public static DBCollection activityCollection = database.getCollection("activity");

    public static void main(String[] args)
    {
        BasicDBObject ob = (BasicDBObject)activityCollection.findOne(new BasicDBObject("name","Garg Pickup"));
        ObjectId hell = (ObjectId) ob.get("_id");
        Activity act = getActivity(hell);
        System.out.println(act.getName());
    }

    public static Activity getActivity(ObjectId ID)
    {
        BasicDBObject object = new BasicDBObject("_id",ID);
        Activity act = new Activity(object);
        return act;
    }

    public static void addNewActivity(Activity activity)
    {
        BasicDBObject object = activity.getDBform();
        activityCollection.insert(object);
    }

    public static void updateActivity(Activity activity)
    {
        activityCollection.update(new BasicDBObject("_id",(ObjectId)activity.getObjectID()),new BasicDBObject("$set",activity.getDBform()));
    }

}
