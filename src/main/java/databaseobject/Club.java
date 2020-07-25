package databaseobject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Club implements DatabaseStructureObject
{
    private DBObject object;
    public static final String NAME = "name", DESCRIPTION = "description", LOCATION_COUNTRY = "location_country", LOCATION_STATE = "location_state",
    LOCATION_CITY = "location_city", CLUB_TYPE = "club_type", ACTIVITY = "activity";

    private static Club databaseConnectivityObject = new Club();
    public static Club databaseConnectivity()
    {
        return databaseConnectivityObject;
    }
    public Club() {}

    public Club(String name, String description, String location_country, String location_state, String location_city, String club_type, ObjectId owner)
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
        object.put("activity", new BasicDBObject());

        this.object = object;
        setMember(owner, true);
    }

    public Object getConciseDataNode()
    {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        //getDBForm().get(NAME)
        return null;
    }


    @Override
    public Object get(String string) {
        return object.get(string);
    }

    public void set(String identifier, Object value)
    {
        object.put(identifier,value);
    }

    public void setActivity(ObjectId act, boolean isAdding)
    {
        if(isAdding)
            ((DBObject)object.get("clubs")).put(act.toHexString(), true);
        else
            ((DBObject)object.get("clubs")).removeField(act.toHexString());
    }

    public void setMember(ObjectId userId, boolean isAdding){
        if(isAdding)
            ((DBObject)object.get("clubs")).put(userId.toHexString(), true);
        else
            ((DBObject)object.get("clubs")).removeField(userId.toHexString());
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
            return new Club(object);
        return null;
    }

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


}
