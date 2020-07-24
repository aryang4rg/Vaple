
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * <PRE> The activity class describes an activity object, which stores data about the activity's name, location, time, description and
 * a list of attendees. The activity class also constructs a corresponding DBObject to store in the database.</PRE>
 */
public class Activity implements DatabaseStructureObject
{
	String name, description;
	double latitude, longitude;
	ArrayList<ObjectId> attending;
	long time_start, time_end;
	BasicDBObject form;
	ObjectId objectID;
	ObjectId associated_club;
	ObjectId creator;

	private static Activity databaseConnectivityObject = new Activity();
	public static Activity databaseConnectivity()
	{
		return databaseConnectivityObject;
	}

	public Activity() {}

	public Activity(String name, String description, ObjectId creator, ArrayList<ObjectId> attending, long time_start, long time_end, double latitude, double longitude, Club associated_club)
	{
		this.name = name;
		this.description = description;
		this.attending = attending;
		this.creator = creator;
		this.time_start = time_start;
		this.time_end = time_end;
		this.latitude = latitude;
		this.longitude = longitude;
		if (associated_club != null) {
			this.associated_club = associated_club.getObjectID();
		}

	}

	public Activity(DBObject object)
	{
		name = (String)object.get("name");
		description = (String)object.get("description");
		objectID = (ObjectId)object.get("_id");
		attending = (ArrayList<ObjectId>)object.get("attending");
		time_start = (Long)object.get("time_start");
		time_end = (Long)object.get("time_end");
		latitude = (double)object.get("latitude");
		longitude = (double)object.get("longitude");
		creator = (ObjectId)object.get("creator");
		associated_club = (ObjectId)object.get("associated_club");
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
	public DatabaseStructureObject getByInfoInDataBase(String varName, Object data)
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
	public boolean infoExistsInDatabase(String varName, Object data)
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

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public BasicDBObject getDBForm()
	{
		form = new BasicDBObject();
		form.append("name",getName());
		form.append("description",getDescription());
		form.append("attending",getAttending());
		form.append("creator", getCreator());
		form.append("time_start",getTime_start());
		form.append("time_end",getTime_end());

		form.append("latitude",getLatitude());
		form.append("longitude",getLongitude());

		form.append("associated_club", getAssociated_club());
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
	public long getStartTime()
	{
		return time_start;
	}

	/**
	 *
	 * @param time Sets the time the activity takes place
	 */
	public void setTime(long time)
	{
		this.time_start = time;
	}

	public long getTime_start() {
		return time_start;
	}

	public void setTime_start(long time_start) {
		this.time_start = time_start;
	}

	public long getTime_end() {
		return time_end;
	}

	public void setTime_end(long time_end) {
		this.time_end = time_end;
	}

	public BasicDBObject getForm() {
		return form;
	}

	public void setForm(BasicDBObject form) {
		this.form = form;
	}

	public ObjectId getAssociated_club() {
		return associated_club;
	}

	public void setAssociated_club(ObjectId associated_club) {
		this.associated_club = associated_club;
	}

	public ObjectId getCreator() {
		return creator;
	}

	public void setCreator(ObjectId creator) {
		this.creator = creator;
	}
}
