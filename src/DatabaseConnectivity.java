import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class DatabaseConnectivity
{
    public static void main(String[] args) throws UnknownHostException
    {

        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("test");
        DBCollection collection = database.getCollection("epic");
        List<Integer> books = Arrays.asList(27464, 747854);
        DBObject person = new BasicDBObject("_id", "jo")
                .append("name", "Jo Bloggs")
                .append("address", new BasicDBObject("street", "123 Fake St")
                        .append("city", "Faketon")
                        .append("state", "MA")
                        .append("zip", 12345))
                .append("books", books);
        collection.insert(person);
    }

}
