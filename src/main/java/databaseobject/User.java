package databaseobject;

import main.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import util.Json;

import java.util.ArrayList;
import java.util.Date;

/**
 * <PRE>The user class describes a user object, which stores the user's name, location, description, password, email, a list of followers and
 * following, a list of the activities they have done, and a unique token.</PRE>
 */
public class User implements DatabaseStructureObject
{
	private DBObject object;

	public static final String NAME = "name", EMAIL = "email", PASSWORD = "password", LOCATION_COUNTRY = "location_country", LOCATION_STATE = "location_state",
	LOCATION_CITY = "location_city", DESCRIPTION = "description", TOKEN = "token", FOLLOWING = "following",
			FOLLOWERS = "followers", ACTIVITIES = "activities", CLUB = "clubs", VERIFIED = "verified", VERIFICATION_TOKEN = "verification_token";

	private boolean verifiedUser = false;
	public int emailsSentToday = 0;

	private static User databaseConnectivityObject = new User();
	public static User databaseConnectivity()
	{
		return databaseConnectivityObject;
	}
	public User() {}

	public User(String name, String email, String password, String location_country,
		String location_state, String location_city, String description, String token, String verification_token)
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
		object.put("verified",verifiedUser);
		object.put("sentEmails", new Long(0));
		object.put(VERIFICATION_TOKEN, verification_token);

		this.object = object;
	}


	public ArrayList<String> getActivitiesList()
	{
		return new ArrayList<String>( ((DBObject)get(ACTIVITIES)).keySet());
	}

	public ArrayList<String> getClubList()
	{
		return new ArrayList<String>( ((DBObject)get(CLUB)).keySet());
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
		node.put("clubs", Json.toJson(dbObjectClubListToArrayList(getClubs())));
		return node;
	}
	public static ArrayList<String> dbObjectClubListToArrayList(DBObject clubs)
	{
		return new ArrayList<>(clubs.keySet());
	}

	public DBObject getClubs()
	{
		return (DBObject) object.get("clubs");
	}



	public void addClubToUser(Club club)
	{
		((DBObject)object.get("clubs")).put(club.getObjectID().toHexString(), true);
	}

	public void addActivityToUser(Activity activity)
	{
		((DBObject)object.get("activities")).put(activity.getObjectID().toHexString(),true);
	}

	public void clearEmailsSent()
	{
		emailsSentToday = 0;
		((DBObject)object.get("sentEmails")).put("day",emailsSentToday);
	}

	public User(DBObject object)
	{
		this.object = object;
	}

	@Override
	public Object get(String string) {
		return object.get(string);
	}

	public void set(String identifier, Object value)
	{
		object.put(identifier,value);
	}

	/**
	 *
	 * @return A DBObject version of the databasestructureobjects.databaseobject.User.
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

	public void setActivities(ObjectId activityId, boolean isAdding){
			if(isAdding)
				((DBObject)object.get(ACTIVITIES)).put(activityId.toHexString(), true);
			else
			((DBObject)object.get(ACTIVITIES)).removeField(activityId.toHexString());
	}

	public void setClubs(ObjectId activityId, boolean isAdding){
		if(isAdding)
			((DBObject)object.get("clubs")).put(activityId.toHexString(), true);
		else
			((DBObject)object.get("clubs")).removeField(activityId.toHexString());
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

	public DatabaseStructureObject findInDatabase(DBObject obj){
		DBObject object = DatabaseConnectivity.findOneObject(obj, DatabaseConnectivity.ACCOUNTCOLLECTION);
		if(object != null)
			return new User(object);
		return null;
	}

	public DatabaseStructureObject getFromInfoInDataBase(String varName, Object data)
	{
		return findInDatabase(new BasicDBObject(varName,data));
	}

	public boolean infoExistsInDatabase(String varName, Object data)
	{
		return getFromInfoInDataBase(varName, data) != null;
	}


	/**
	 *
	 * @param user given a user, goes into database find the previous version of that user by its id and updates it
	 */
	public void updateInDatabase(DatabaseStructureObject user)
	{
		DatabaseConnectivity.updateObject(user.getDBForm(), DatabaseConnectivity.ACCOUNTCOLLECTION);
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

	public boolean isVerifiedUser()
	{
		return verifiedUser;
	}

	public void setVerifiedUser(boolean verifiedUser)
	{
		this.verifiedUser = verifiedUser;
	}

	public long getLastEmailTime()
	{
		Long l = (Long)object.get("sentEmails");
		return l;
	}

	public void setLastEmailTime()
	{
		set("sentEmails",System.currentTimeMillis());
		updateInDatabase(this);
	}

	public ArrayList<String> makeUnique(ArrayList<String> mutuals, ArrayList<String> current)
	{
		for (int i = mutuals.size() - 1; i >= 0; i--)
		{
			for (int j = 0; j < current.size(); j++)
			{
				if (mutuals.get(i).equals(current.get(j)))
				{
					mutuals.remove(i);
					break;
				}
			}
		}

		return mutuals;
	}

	public ArrayList<ObjectId> mutualFriends(ObjectId id)
	{
		User user = (User)getFromInfoInDataBase("_id",id);
		ArrayList<String> mutuals = user.getFollowing();
		ArrayList<String> currentFriends = getFollowing();

		mutuals = makeUnique(mutuals,currentFriends);

		ArrayList<ObjectId> mutualFriends = new ArrayList<ObjectId>();

		for (int i = 0; i < mutuals.size(); i++)
		{
			mutualFriends.add(new ObjectId(mutuals.get(i)));
		}
		return mutualFriends;
	}

	public ArrayList<ObjectId> mutualActivities(ObjectId id)
	{
		User user = (User)getFromInfoInDataBase("_id",id);
		ArrayList<String> mutuals = user.getActivitiesList();
		ArrayList<String> currentActivities = getActivitiesList();

		mutuals = makeUnique(mutuals,currentActivities);

		ArrayList<ObjectId> mutualActivities = new ArrayList<ObjectId>();

		for (int i = 0; i < mutuals.size(); i++)
		{
			mutualActivities.add(new ObjectId(mutuals.get(i)));
		}

		return mutualActivities;
	}

	public ArrayList<ObjectId> mutualClubs(ObjectId id)
	{
		User user = (User)getFromInfoInDataBase("_id",id);
		ArrayList<String> mutuals = user.getClubList();
		ArrayList<String> currentClubs = getClubList();

		mutuals = makeUnique(mutuals,currentClubs);

		ArrayList<ObjectId> mutualClubs = new ArrayList<ObjectId>();

		for (int i = 0; i < mutuals.size(); i++)
		{
			mutualClubs.add(new ObjectId(mutuals.get(i)));
		}

		return mutualClubs;
	}

}