import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;

public class User
{
    String name, email, id, password, location_country, location_state, location_city, description, token;
    ArrayList<String> followers, following, activities;
    BasicDBObject form;

    public User(String name, String email, String id, String password, String location_country, String location_state, String location_city, String description, String token, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> activities)
    {
        this.name = name;
        this.email = email;
        this.id = id;
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
        id = (String)object.get("_id");
        password = (String)object.get("password");
        location_country = (String)object.get("location_country");
        location_city = (String)object.get("location_city");
        location_state = (String)object.get("location_state");
        description = (String)object.get("description");
    }

    public User(BasicDBObject object)
    {
        name = (String)object.get("name");
        email = (String)object.get("email");
        id = (String)object.get("_id");
        password = (String)object.get("password");
        location_country = (String)object.get("location_country");
        location_city = (String)object.get("location_city");
        location_state = (String)object.get("location_state");
        description = (String)object.get("description");
    }

    public BasicDBObject getDBForm()
    {
        form = new BasicDBObject();
        form.append("name",getName());
        form.append("email",getEmail());
        form.append("password",getPassword());
        form.append("_id",getId());
        form.append("location_county",getLocation_country());
        form.append("location_state",getLocation_state());
        form.append("location_city",getLocation_city());
        form.append("description",getDescription());

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<String> activities) {
        this.activities = activities;
    }
}