import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * <PRE>The user class describes a user object, which stores the user's name, location, description, password, email, a list of followers and
 * following, a list of the activities they have done, and a unique token.</PRE>
 */
public class User implements DatabaseStructureObject
{
	private DBObject object;

	public static final String NAME = "name", EMAIL = "email", PASSWORD = "password", LOCATION_COUNTRY = "location_country", LOCATION_STATE = "location_state",
	LOCATION_CITY = "location_city", DESCRIPTION = "description", TOKEN = "token", FOLLOWING = "following", FOLLOWERS = "followers", ACTIVITIES = "activities";



	private static User databaseConnectivityObject = new User();
	public static User databaseConnectivity()
	{
		return databaseConnectivityObject;
	}
	public User() {}

	public User(String name, String email, String password, String location_country,
		String location_state, String location_city, String description, String token)
	{
		DBObject object = new BasicDBObject();

		object.put("name", name);
		object.put("email", email);
		object.put("password", password);
		object.put("location_country", location_country);
		object.put("location_state", location_state);
		object.put("location_city", location_city);
		object.put("description", description);
		object.put("token", token);
		object.put("following", new BasicDBObject());
		object.put("followers", new BasicDBObject());
		object.put("activities", new BasicDBObject());
		object.put("clubs",new BasicDBObject());

		this.object = object;
	}

	public ArrayList<DBObject> getActivities(int limit)
	{
		return (ArrayList<DBObject>) DatabaseConnectivity.ACTIVITYCOLLECTION.find()
		.sort(new BasicDBObject("time",1)).limit(limit).toArray();
	}

	public ArrayList<DBObject> getClubs()
	{
		return (ArrayList<DBObject>) object.get("clubs");
	}

	public void addClubToUser(Club club)
	{
		((DBObject)object.get("clubs")).put(club.getObjectID().toHexString(), true);
	}

	public void addActivityToUser(Activity activity)
	{
		((DBObject)object.get("activities")).put(activity.getObjectID().toHexString(),true);
	}

	public User(DBObject object)
	{
		this.object = object;
	}

	/**
	 *
	 * @return A DBObject version of the databasestructureobjects.User.
	 */
	public DBObject getDBForm()
	{
		return object;
	}

	/**
	 *
	 * @return Returns the name of the user
	 */
	public String getName() {
		return (String)object.get("name");
	}

	/**
	 *
	 * @param name Sets the name of the user
	 */
	public void setName(String name) {
		object.put("name", name);
	}

	/**
	 *
	 * @return Returns the email of the user
	 */
	public String getEmail() {
		return (String)object.get("email");
	}

	/**
	 *
	 * @param email Sets the email of the user
	 */
	public void setEmail(String email) {
		object.put("email", email);
	}

	/**
	 *
	 * @return Returns the password of the user
	 */
	public String getPassword() {
		return (String)object.get("password");
	}

	/**
	 *
	 * @param password Sets the password of the user
	 */
	public void setPassword(String password) {
		object.put("password", password);
	}

	/**
	 *
	 * @return Returns the country of the user
	 */
	public String getCountry() {
		return (String)object.get("location_country");
	}

	/**
	 *
	 * @param location_country Sets the country of the user
 	 */
	public void setCountry(String location_country) {
		object.put("location_country", location_country);
	}

	/**
	 *
	 * @return Returns the state(location) of the user
	 */
	public String getState() {
		return (String)object.get("location_state");
	}

	/**
	 *
	 * @param location_state Sets the state of the user
	 */
	public void setState(String location_state) {
		object.put("location_state", location_state);
	}

	/**
	 *
	 * @return Returns the city of the user
	 */
	public String getCity() {
		return (String)object.get("location_city");
	}

	/**
	 *
	 * @param location_city Sets the city of the user
	 */
	public void setCity(String location_city) {
		object.put("location_city", location_city);
	}

	/**
	 *
	 * @return Returns the description of the user
	 */
	public String getDescription() {
		return (String)object.get("description");
	}

	/**
	 *
	 * @param description Sets the description of the user
	 */
	public void setDescription(String description) {
		object.put("description", description);
	}

	/**
	 *
	 * @return Returns the unique token of the user
	 */
	public String getToken() {
		return (String)object.get("token");
	}

	/**
	 *
	 * @param token Sets the token of the user
	 */
	public void setToken(String token) {
		object.put("token", token);
	}

	/**
	 *
	 * @return Returns the ObjectId of the user
	 */
	public ObjectId getObjectID()
	{
		return (ObjectId)object.get("_id");
	}

	/**
	 * Sets another object as a follower of this user
	 * @param follower The other user
	 * @param following True if they are following this user, false otherwise
	 */
	public void setFollower(ObjectId follower, boolean following){
		if(following)
			((DBObject)object.get("followers")).put(follower.toHexString(), true);
		else
			((DBObject)object.get("followers")).removeField(follower.toHexString());
	}

	/**
	 * Sets if this user is following another user
	 * @param following The other user being followed
	 * @param bfollowing True if this user is following the other, false otherwise
	 */
	public void setFollowing(ObjectId following, boolean bfollowing){
		if(bfollowing)
			((DBObject)object.get("following")).put(following.toHexString(), true);
		else
			((DBObject)object.get("following")).removeField(following.toHexString());
	}

	/**
	 * Returns whether this user is following another user
	 * @param user The other user
	 * @return Returns true if they are following the other user, false otherwise
	 */
	public boolean isFollowing(ObjectId user){
		return ((DBObject)object.get("following")).get(user.toHexString()) != null;
	}

	/**
	 * Returns if this user is followed by another specified user
	 * @param user The other user
	 * @return Returns true if this user is being followed by the other user, false otherwise
	 */
	public boolean isFollowedBy(ObjectId user){
		return ((DBObject)object.get("followers")).get(user.toHexString()) != null;
	}

	public ArrayList<String> getFollowing()
	{
		DBObject followingObject = (DBObject)object.get("following");
		return new ArrayList<String>(followingObject.keySet());
	}

	public int countObjectEntries(DBObject object){
		int size = object.keySet().size();

		if(object.get("_id") != null)
			size--;
		return size;
	}

	/**
	 *
	 * @return Returns the total number of followers
	 */
	public int countFollowers(){
		return countObjectEntries((DBObject)object.get("followers"));
	}

	/**
	 *
	 * @return Returns the total number of users followed
	 */
	public int countFollowing(){
		return countObjectEntries((DBObject)object.get("following"));
	}

	public ObjectNode toAccountNode(){
		ObjectNode node = JsonNodeFactory.instance.objectNode();

		node.put("name", getName());
		node.put("id", getObjectID().toHexString());

		return node;
	}

	public ObjectNode toProfileNode(){
		ObjectNode node = JsonNodeFactory.instance.objectNode();

		node.put("name", getName());
		node.put("id", getObjectID().toHexString());
		node.put("location_country", getCountry());
		node.put("location_state", getState());
		node.put("location_city", getCity());
		node.put("description", getDescription());
		node.put("followers_count", countFollowers());
		node.put("following_count", countFollowing());

		return node;
	}



	public DatabaseStructureObject findInDatabase(DBObject obj){
		DBObject object = DatabaseConnectivity.findOneObject(obj, DatabaseConnectivity.ACCOUNTCOLLECTION);
		if(object != null)
			return new User(object);
		return null;
	}

	public DatabaseStructureObject getByInfoInDataBase(String varName, Object data)
	{
		return findInDatabase(new BasicDBObject(varName,data));
	}

	public boolean infoExistsInDatabase(String varName, Object data)
	{
		return getByInfoInDataBase(varName, data) != null;
	}


	/**
	 *
	 * @param user given a user, goes into database find the previous version of that user by its id and updates it
	 */
	public void updateInDatabase(DatabaseStructureObject user)
	{
		DatabaseConnectivity.updateObject( user.getDBForm(), DatabaseConnectivity.ACCOUNTCOLLECTION);
	}


	public void addInDatabase(DatabaseStructureObject user)
	{
		String email = ((User)(user)).getEmail();
		if (DatabaseConnectivity.findOneObject(  new BasicDBObject("email", email), DatabaseConnectivity.ACCOUNTCOLLECTION) == null)
		{
			DatabaseConnectivity.addObject(user.getDBForm(), DatabaseConnectivity.ACCOUNTCOLLECTION);
		}
		else
		{
			throw new RuntimeException("Account with email " + email + " has already been created!");
		}
	}

}