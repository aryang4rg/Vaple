import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Club implements DatabaseStructureObject
{
    private DBObject object;
    public static final String NAME = "name", DESCRIPTION = "description", LOCATION_COUNTRY = "location_country", LOCATION_STATE = "location_state",
    LOCATION_CITY = "location_city", CLUB_TYPE = "club_type";

    private static Club databaseConnectivityObject = new Club();
    public static Club databaseConnectivity()
    {
        return databaseConnectivityObject;
    }
    public Club() {}

    public Club(String name, String description, String location_country, String location_state, String location_city, String club_type, DBObject owner)
    {
        DBObject object = new BasicDBObject();

        object.put("name",name);
        object.put("description",description);
        object.put("location_country",location_country);
        object.put("location_state",location_state);
        object.put("location_city",location_city);
        object.put("club_type",club_type);
        object.put("members",new BasicDBObject());
        object.put("owner",owner);

        this.object = object;
    }

    public int countObjectEntries(DBObject object){
        int size = object.keySet().size();

        if(object.get("_id") != null)
            size--;
        return size;
    }

    public int countMembers()
    {
        return countObjectEntries((DBObject)object.get("followers"));
    }

    public Club(DBObject object)
    {
        this.object = object;
    }

    public ArrayList<DBObject> getMembers()
    {
        return (ArrayList<DBObject>) DatabaseConnectivity.CLUBCOLLECTION.find().sort(new BasicDBObject("name",1)).toArray();
    }


    @Override
    public DatabaseStructureObject findInDatabase(DBObject obj)
    {
        DBObject object = DatabaseConnectivity.findOneObject(obj, DatabaseConnectivity.CLUBCOLLECTION);
        if(object != null)
            return new User(object);
        return null;
    }

    @Override
    public DatabaseStructureObject getByInfoInDataBase(String varName, Object data)
    {
        return findInDatabase(new BasicDBObject(varName,data));
    }

    @Override
    public boolean infoExistsInDatabase(String varName, Object data)
    {
        return getByInfoInDataBase(varName, data) != null;
    }

    @Override
    public void updateInDatabase(DatabaseStructureObject object)
    {
        DatabaseConnectivity.updateObject(object.getDBForm(), DatabaseConnectivity.CLUBCOLLECTION);
    }

    @Override
    public void addInDatabase(DatabaseStructureObject object)
    {
        DatabaseConnectivity.addObject(object.getDBForm(), DatabaseConnectivity.CLUBCOLLECTION);
    }

    @Override
    public DBObject getDBForm()
    {
        return object;
    }

    @Override
    public ObjectId getObjectID()
    {
        return (ObjectId)object.get("_id");
    }

    public static String getNAME()
    {
        return NAME;
    }

    public static String getDESCRIPTION()
    {
        return DESCRIPTION;
    }

    public static String getLocationCountry()
    {
        return LOCATION_COUNTRY;
    }

    public static String getLocationState()
    {
        return LOCATION_STATE;
    }

    public static String getLocationCity()
    {
        return LOCATION_CITY;
    }

    public static String getClubType()
    {
        return CLUB_TYPE;
    }
}
