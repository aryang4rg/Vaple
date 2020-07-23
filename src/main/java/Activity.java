import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import javax.xml.crypto.Data;
import java.util.ArrayList;

/**
 * <PRE> The activity class describes an activity object, which stores data about the activity's name, location, time, description and
 * a list of attendees. The activity class also constructs a corresponding DBObject to store in the database.</PRE>
 */
public class Activity implements DatabaseStructureObject
{
	String name, location_country, location_state, location_city, description;
	ArrayList<ObjectId> attending;
	long time;
	BasicDBObject form;
	ObjectId objectID;

	private static Activity databaseConnectivityObject = new Activity();
	public static Activity databaseConnectivity()
	{
		return databaseConnectivityObject;
	}

	public Activity() {}

	public Activity(String name, String location_country, String location_state, String location_city, String description, ArrayList<ObjectId> attending, long time)
	{
		this.name = name;
		this.location_country = location_country;
		this.location_state = location_state;
		this.location_city = location_city;
		this.description = description;
		this.attending = attending;
		this.time = time;
	}

	public Activity(DBObject object)
	{
		name = (String)object.get("name");
		location_country = (String)object.get("location_country");
		location_city = (String)object.get("location_city");
		location_state = (String)object.get("location_state");
		description = (String)object.get("description");
		objectID = (ObjectId)object.get("_id");
		attending = (ArrayList<ObjectId>)object.get("attending");
		time = (Long)object.get("time");
	}

	public Activity(BasicDBObject object)
	{
		name = (String)object.get("name");
		location_country = (String)object.get("location_country");
		location_city = (String)object.get("location_city");
		location_state = (String)object.get("location_state");
		description = (String)object.get("description");
		objectID = (ObjectId)object.get("_id");
		attending = (ArrayList<ObjectId>)object.get("attending");
		time = (Long)object.get("time");
	}

	/**
	 * Finds an activity in the database
	 * @param obj The activity to find
	 * @return Returns the activity object
	 */
	@Override
	public DatabaseStructureObject findInDatabase(DBObject obj)
	{
		DBObject object = DatabaseConnectivity.findOneObject(obj, DatabaseConnectivity.ACTIVITYCOLLECTION);
		if(object != null)
			return new Activity(object);
		return null;
	}

	/**
	 * Finds an activity via info
	 * @param varName The property type
	 * @param data The value of the property
	 * @return The activity object
	 */
	@Override
	public DatabaseStructureObject getByInfoInDataBase(String varName, String data)
	{
		return findInDatabase(new BasicDBObject(varName,data));

	}

	/**
	 * Finds if an activity exists in the database through provided info
	 * @param varName The property type
	 * @param data The value of the property
	 * @return True if it exists in the database, false if it doesn't
	 */
	@Override
	public boolean infoExistsInDatabase(String varName, String data)
	{
		return getByInfoInDataBase(varName, data) != null;
	}

	/**
	 * Updates a specified object in the database
	 * @param object The updated object
	 */
	@Override
	public void updateInDatabase(DatabaseStructureObject object)
	{
		DatabaseConnectivity.updateObject(object.getDBForm(), DatabaseConnectivity.ACTIVITYCOLLECTION);
	}

	/**
	 * Adds an activity into the database
	 * @param object The object to add
	 */
	@Override
	public void addInDatabase(DatabaseStructureObject object)
	{
		DatabaseConnectivity.addObject(object.getDBForm(), DatabaseConnectivity.ACTIVITYCOLLECTION);
	}

	public BasicDBObject getDBForm()
	{
		form = new BasicDBObject();
		form.append("name",getName());
		form.append("location_county", getCountry());
		form.append("location_state",getState());
		form.append("location_city",getCity());
		form.append("description",getDescription());
		form.append("attending",getAttending());
		form.append("time",getTime());

		return form;
	}

	/**
	 *
	 * @return Gets the name of the activity
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *
	 * @param name Sets the name of the activity
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *
	 * @return Gets the country the activity takes place in
	 */
	public String getCountry()
	{
		return location_country;
	}

	/**
	 *
	 * @param location_country Sets the country of the activity
	 */
	public void setCountry(String location_country)
	{
		this.location_country = location_country;
	}

	/**
	 *
	 * @return Returns the state the activity takes place in
	 */
	public String getState()
	{
		return location_state;
	}

	/**
	 *
	 * @return Returns the ObjectId of the activity
	 */
	public ObjectId getObjectID()
	{
		return objectID;
	}

	/**
	 *
	 * @param objectID Sets the ObjectId of an Object
	 */
	public void setObjectID(ObjectId objectID)
	{
		this.objectID = objectID;
	}

	/**
	 *
	 * @param location_state Sets the state(location) of the activity
	 */
	public void setState(String location_state)
	{
		this.location_state = location_state;
	}

	/**
	 *
	 * @return Returns the city the activity takes place in
	 */
	public String getCity()
	{
		return location_city;
	}

	/**
	 *
	 * @param location_city Sets the city the activity takes place in
	 */
	public void setCity(String location_city)
	{
		this.location_city = location_city;
	}

	/**
	 *
	 * @return Returns the description of the activity
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *
	 * @param description Sets the description of the activity
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *
	 * @return Returns a list of ObjectId of attendees for the activity
	 */
	public ArrayList<ObjectId> getAttending()
	{
		return attending;
	}

	/**
	 *
	 * @param attending Sets the list of attendees for an activity
	 */
	public void setAttending(ArrayList<ObjectId> attending)
	{
		this.attending = attending;
	}

	/**
	 *
	 * @param id Adds an attendee to the list of attendees
	 */
	public void addAttending(ObjectId id) { attending.add(id);}

	/**
	 *
	 * @param id Removes an attendee from the list of attendees
	 */
	public void removeAttending(ObjectId id) { attending.remove(id); }

	/**
	 *
	 * @return Returns the time the activity takes place
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 *
	 * @param time Sets the time the activity takes place
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

}
