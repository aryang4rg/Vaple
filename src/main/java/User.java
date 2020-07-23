import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public class User
{
	private DBObject object;

	public User(String name, String email, String password, String location_country,
		String location_state, String location_city, String description, String token)
	{
		DBObject object = new BasicDBObject();

		object.put("name", name);
		object.put("email", email);
		object.put("password", password);
		object.put("location_state", location_state);
		object.put("location_city", location_city);
		object.put("description", description);
		object.put("token", token);
		object.put("following", new BasicDBObject());
		object.put("followers", new BasicDBObject());
		object.put("activities", new BasicDBObject());

		this.object = object;
	}

	public User(DBObject object)
	{
		this.object = object;
	}

	public DBObject getDBForm()
	{
		return object;
	}

	public String getName() {
		return (String)object.get("name");
	}

	public void setName(String name) {
		object.put("name", name);
	}

	public String getEmail() {
		return (String)object.get("email");
	}

	public void setEmail(String email) {
		object.put("email", email);
	}

	public String getPassword() {
		return (String)object.get("password");
	}

	public void setPassword(String password) {
		object.put("password", password);
	}

	public String getCountry() {
		return (String)object.get("location_country");
	}

	public void setCountry(String location_country) {
		object.put("location_country", location_country);
	}

	public String getState() {
		return (String)object.get("location_state");
	}

	public void setState(String location_state) {
		object.put("location_state", location_state);
	}

	public String getCity() {
		return (String)object.get("location_city");
	}

	public void setCity(String location_city) {
		object.put("location_city", location_city);
	}

	public String getDescription() {
		return (String)object.get("description");
	}

	public void setDescription(String description) {
		object.put("description", description);
	}

	public String getToken() {
		return (String)object.get("token");
	}

	public void setToken(String token) {
		object.put("token", token);
	}

	public ObjectId getObjectID()
	{
		return (ObjectId)object.get("_id");
	}

	public void setFollower(ObjectId follower, boolean following){
		if(following)
			((DBObject)object.get("followers")).put(follower.toHexString(), true);
		else
			((DBObject)object.get("followers")).removeField(follower.toHexString());
	}

	public void setFollowing(ObjectId following, boolean bfollowing){
		if(bfollowing)
			((DBObject)object.get("following")).put(following.toHexString(), true);
		else
			((DBObject)object.get("following")).removeField(following.toHexString());
	}

	public boolean isFollowing(ObjectId user){
		return ((DBObject)object.get("following")).get(user.toHexString()) != null;
	}

	public boolean isFollowedBy(ObjectId user){
		return ((DBObject)object.get("followers")).get(user.toHexString()) != null;
	}

	public int countObjectEntries(DBObject object){
		int size = object.keySet().size();

		if(object.get("_id") != null)
			size--;
		return size;
	}

	public int countFollowers(){
		return countObjectEntries((DBObject)object.get("followers"));
	}

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
}