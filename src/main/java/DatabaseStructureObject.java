import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public interface DatabaseStructureObject
{
    DatabaseStructureObject findInDatabase(DBObject object);
    DatabaseStructureObject getByInfoInDataBase(String varName, Object data);
    boolean infoExistsInDatabase(String varName, Object data);
    void updateInDatabase(DatabaseStructureObject object);
    void addInDatabase(DatabaseStructureObject object);
    DBObject getDBForm();
    ObjectId getObjectID();

}
