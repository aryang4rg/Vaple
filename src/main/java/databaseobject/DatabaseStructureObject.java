package databaseobject;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public interface DatabaseStructureObject
{
    String ID = "_id";
    DatabaseStructureObject findInDatabase(DBObject object);
    DatabaseStructureObject getFromInfoInDataBase(String varName, Object data);
    boolean infoExistsInDatabase(String varName, Object data);
    void updateInDatabase(DatabaseStructureObject object);
    void addInDatabase(DatabaseStructureObject object);
    DBObject getDBForm();
    ObjectId getObjectID();
    Object get(String identifier);
    void set(String identifier, Object object);

}
