import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Objects;

public class User
{
    String name, email, password, location_country, location_state, location_city, description, token;
    ArrayList<ObjectId> followers, following, activities;
    BasicDBObject form;
    ObjectId objectID;

    public User(String name, String email, String password, String location_country, String location_state, String location_city, String description, String token, ArrayList<ObjectId> followers, ArrayList<ObjectId> following, ArrayList<ObjectId> activities)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.location_country = location_country;
        this.location_state = location_state;
        this.location_city = location_city;
        this.description = description;
        this.token = token;
        this.followers = followers;
        this.following = following;
        this.activities = activities;
    }

    public User(DBObject object)
    {
        name = (String)object.get("name");
        email = (String)object.get("email");
        password = (String)object.get("password");
        location_country = (String)object.get("location_country");
        location_city = (String)object.get("location_city");
        location_state = (String)object.get("location_state");
        description = (String)object.get("description");
        objectID = (ObjectId) object.get("_id");
        following = (ArrayList<ObjectId>) object.get("following");
        followers = (ArrayList<ObjectId>) object.get("followers");
        activities = (ArrayList<ObjectId>) object.get("activties");
        token = (String)object.get("token");
    }

    public User(BasicDBObject object)
    {
        name = (String)object.get("name");
        email = (String)object.get("email");
        password = (String)object.get("password");
        location_country = (String)object.get("location_country");
        location_city = (String)object.get("location_city");
        location_state = (String)object.get("location_state");
        description = (String)object.get("description");
        objectID = (ObjectId)object.get("_id");
        following = (ArrayList<ObjectId>) object.get("following");
        followers = (ArrayList<ObjectId>) object.get("followers");
        activities = (ArrayList<ObjectId>) object.get("activties");
        token = (String)object.get("token");
    }

    public BasicDBObject getDBForm()
    {
        form = new BasicDBObject();
        form.append("name",getName());
        form.append("email",getEmail());
        form.append("password",getPassword());
        form.append("location_county",getLocation_country());
        form.append("location_state",getLocation_state());
        form.append("location_city",getLocation_city());
        form.append("description",getDescription());
        form.append("following",getFollowing());
        form.append("followers",getFollowers());
        form.append("activities",getActivities());

        return form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) {
        this.location_country = location_country;
    }

    public String getLocation_state() {
        return location_state;
    }

    public void setLocation_state(String location_state) {
        this.location_state = location_state;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<ObjectId> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<ObjectId> followers) {
        this.followers = followers;
    }

    public ArrayList<ObjectId> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<ObjectId> following) {
        this.following = following;
    }

    public ArrayList<ObjectId> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<ObjectId> activities) {
        this.activities = activities;
    }

    public ObjectId getObjectID()
    {
        return objectID;
    }

	public ObjectNode getObjectNode(){
		ObjectNode node = JsonNodeFactory.instance.objectNode();

		node.put("name",getName());
        node.put("location_county",getLocation_country());
        node.put("location_state",getLocation_state());
        node.put("location_city",getLocation_city());
        node.put("description",getDescription());
		node.put("id",getObjectID().toHexString());

		return node;
	}
}