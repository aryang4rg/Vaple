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
    public static DBCollection accountCollection = database.getCollection("account");

    public static void main(String[] args)
    {

    }

    public static void addNewUser(User user)
    {
        String email = user.getEmail();
        if (accountCollection.findOne(new BasicDBObject("email",email)) == null)
        {
            BasicDBObject object = user.getDBForm();
            accountCollection.insert(object);
        }
        else
        {
            assert false;
        }
    }

    public static boolean emailAlreadyExists(String email)
    {
        return accountCollection.findOne(new BasicDBObject("email",email)) != null;
    }

    public User getUserByEmail(String email)
    {
        BasicDBObject object = (BasicDBObject)accountCollection.findOne(new BasicDBObject("email",email));
        User user = new User(object);
        return user;
    }

    public static void updateUser(User user)
    {
        accountCollection.update(new BasicDBObject("_id",(ObjectId)user.getObjectID()),new BasicDBObject("$set",user.getDBForm()));
    }

    public static User getUser(String id)
    {
        BasicDBObject obj = new BasicDBObject();
        obj.append("_id",id);
        DBObject obj2 = accountCollection.findOne(obj);

        User user = new User(obj2);
        return user;
    }

}