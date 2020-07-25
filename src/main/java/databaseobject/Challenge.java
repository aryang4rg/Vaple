package databaseobject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import main.DatabaseConnectivity;
import org.bson.types.ObjectId;


public class Challenge implements DatabaseStructureObject
{
    public static final String NAME = "name", DESCRIPTION = "description", FORM = "form", OBJECTID = "objectID", TYPE = "type";
    DBObject object = new BasicDBObject();

    public Challenge(String name, String description, String type)
    {
        object.put("name",name);
        object.put("description",description);
        object.put("type",type);
    }

    public static Challenge databaseConnectivityObject = new Challenge();

    public static Challenge databaseConnectivity()
    {
        return databaseConnectivityObject;
    }

    public Challenge() {}

    @Override
    public DatabaseStructureObject findInDatabase(DBObject obj)
    {
        DBObject object = DatabaseConnectivity.findOneObject(obj, DatabaseConnectivity.ACTIVITYCOLLECTION);
        if(object != null)
            return new Challenge(object);
        return null;
    }

    public Challenge(DBObject object) {this.object = object;}

    @Override
    public DatabaseStructureObject getFromInfoInDataBase(String varName, Object data)
    {
        return findInDatabase(new BasicDBObject(varName,data));
    }

    @Override
    public boolean infoExistsInDatabase(String varName, Object data)
    {
        return getFromInfoInDataBase(varName, data) != null;
    }

    @Override
    public void updateInDatabase(DatabaseStructureObject object)
    {
        DatabaseConnectivity.updateObject(object.getDBForm(), DatabaseConnectivity.CHALLENGECOLLECTION);
    }

    @Override
    public void addInDatabase(DatabaseStructureObject object)
    {
        DatabaseConnectivity.addObject(object.getDBForm(), DatabaseConnectivity.CHALLENGECOLLECTION);
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

    @Override
    public Object get(String identifier)
    {
        return object.get(identifier);
    }

    @Override
    public void set(String identifier, Object value)
    {
        object.put(identifier,value);
    }
}
