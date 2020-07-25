package databaseobject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DB;
import main.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import util.Json;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Club implements DatabaseStructureObject
{
    private DBObject object;
    public static final String NAME = "name", DESCRIPTION = "description", LOCATION_COUNTRY = "location_country", LOCATION_STATE = "location_state",
    LOCATION_CITY = "location_city", CLUB_TYPE = "type", ACTIVITY = "activity", OWNER = "owner", MEMBER = "members";
    List<String> tags;

    private static Club databaseConnectivityObject = new Club();
    public static Club databaseConnectivity()
    {
        return databaseConnectivityObject;
    }
    public Club() {}

    public ObjectNode getConciseDataNode(){

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", (String)get(NAME));
        node.put("id", getObjectID().toHexString());
        node.put("description", (String)get(DESCRIPTION));
        node.put("country", (String)get(LOCATION_COUNTRY));
        node.put("city", (String)get(LOCATION_CITY));
        node.put("state", (String)get(LOCATION_STATE));
        node.put("country", (String)get(LOCATION_COUNTRY));
        node.put(CLUB_TYPE, (String)get(CLUB_TYPE));
        node.put("owner", ((ObjectId)get(OWNER)).toHexString());

        ObjectNode actNode = Util.createObjectNode();
        ArrayList<String> activities = getActivities();
        for (String str : activities)
        {
            actNode.put("str", true);
        }

        ArrayNode memNode = new ArrayNode(JsonNodeFactory.instance);
        ArrayList<DBObject> members = getMembers();
        for (DBObject mem : members)
        {
            memNode.add( ((ObjectId) mem.get(ID)).toHexString());
        }

        node.put(MEMBER, memNode);


        return node;
    }
    public Club(String name, String description, String location_country, String location_state, String location_city, String club_type, ObjectId owner)
    {
        DBObject object = new BasicDBObject();

        object.put("name",name);
        object.put("description",description);
        object.put("location_country",location_country);
        object.put("location_state",location_state);
        object.put("location_city",location_city);
        object.put(CLUB_TYPE,club_type);
        object.put("members",new BasicDBObject());
        object.put("owner",owner);
        object.put("activity", new BasicDBObject());
        object.put("tags",tags);


        this.object = object;
    }

    public ArrayList<String> getActivities()
    {
        Set<String> activities = ((DBObject)(object.get("activities"))).keySet();
        ArrayList<String> actList = new ArrayList<>(activities);
        return actList;
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

    public void addTag(String tag)
    {
        tags = (ArrayList<String>)object.get("tags");
        tags.add(tag);
        object.put("tags",tags);
    }

    public void removeTag(String tag)
    {
        tags = (ArrayList<String>)object.get("tags");
        tags.remove(tag);
        object.put("tags",tags);
    }

    public ArrayList<String> getTags()
    {
        return (ArrayList<String>)object.get("tags");
    }

    public static ArrayList<Club> getActivitiesByTag(String tag)
    {
        ArrayList<DBObject> objects = (ArrayList<DBObject>)DatabaseConnectivity.findObjectsByCommonProperty("tags",tag, DatabaseConnectivity.CLUBCOLLECTION);
        ArrayList<Club> clubs = new ArrayList<Club>();
        for (int i = 0; i < objects.size(); i++)
            clubs.add(new Club(objects.get(i)));

        return clubs;
    }
}
