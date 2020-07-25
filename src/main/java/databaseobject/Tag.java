package databaseobject;

import java.util.ArrayList;

public class Tag
{
    ArrayList<String> tags;

    public Tag()
    {
        tags = new ArrayList<String>();
        tags.add("Fundraising");
        tags.add("Garbage Cleanup");
        tags.add("Charity Work");
        tags.add("Environmental Care");
        tags.add("Environmental Cleanup");
        tags.add("Hospital Volunteering");
        tags.add("Animal Care");
        tags.add("Awareness Work");
        tags.add("Religious Work");
        tags.add("Religious Volunteering");
        tags.add("Elderly Care");
        tags.add("Disabled Care");
        tags.add("Special Needs Care");
        tags.add("Homeless Volunteer Work");
        tags.add("Social Issue Awareness");
        tags.add("Protest");
        tags.add("Educational Workshops");
    }

    public ArrayList<String> getTags()
    {
        return tags;
    }

    public boolean checkIfTagExists(String tag)
    {
        boolean exists = false;
        for (int i = 0; i < tags.size(); i++)
            if (tags.get(i).equals(tag))
            {
                exists = true;
                break;
            }
        return exists;
    }
}
