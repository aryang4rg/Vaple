import com.mongodb.*;
import org.bson.BSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class DatabaseConnectivity
{
    private static DBCollection collection;
    public static void main(String[] args) throws UnknownHostException
    {
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("login");
        collection = database.getCollection("account");
    }

    public static void addUser(User user, DBCollection collection)
    {
        String email = user.getEmail();
        if (collection.findOne(new BasicDBObject("email",email)) == null)
        {
            BasicDBObject object = user.getDBForm();
            collection.insert(object);
        }
    }

    public static User getUser(String id)
    {
        BasicDBObject obj = new BasicDBObject();
        obj.append("_id",id);
        DBObject obj2 = collection.findOne(obj);

        User user = new User(obj2);
        return user;
    }

}