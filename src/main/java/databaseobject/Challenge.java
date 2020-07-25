package databaseobject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import main.DatabaseConnectivity;
import org.bson.types.ObjectId;

import java.util.ArrayList;

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
        return null;
    }

    @Override
    public boolean infoExistsInDatabase(String varName, Object data)
    {
        return false;
    }

    @Override
    public void updateInDatabase(DatabaseStructureObject object)
    {

    }

    @Override
    public void addInDatabase(DatabaseStructureObject object)
    {

    }

    @Override
    public DBObject getDBForm()
    {
        return object;
    }

    @Override
    public ObjectId getObjectID()
    {
        return null;
    }

    @Override
    public Object get(String identifier)
    {
        return null;
    }

    @Override
    public void set(String identifier, Object object)
    {

    }
}
