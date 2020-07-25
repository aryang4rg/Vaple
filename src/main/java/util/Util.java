package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Util{
	private Util(){}

	public static ObjectNode createObjectNode(){
		return JsonNodeFactory.instance.objectNode();
	}

	/* useful for email and password */
	public static String nullIfSpecialCharacters(String string){
		/* all characters on the keyboard allowed */
		for(int i = 0; i < string.length(); i++)
			if(string.charAt(i) < 0x20 || string.charAt(i) >= 0x7f)
				return null;
		return string;
	}

	/* useful for username and location */
	public static String removeNonAlphanumeric(String string){
		/* does not remove - or _ or space */
		return string.replaceAll("[^ a-zA-Z0-9_-]", "");
	}

	/* useful for checking if a string is empty */
	public static String trimString(String string){
		if(string == null)
			return null;
		string = string.trim();

		if(string.length() == 0)
			return null;
		return string;
	}

	public static String removeTrimAndNonAlphanumeric(String string){
		string = trimString(string);

		if(string == null)
			return null;
		string = removeNonAlphanumeric(string);

		if(string.length() == 0)
			return null;
		return string;
	}

	public static String trimAndnullIfSpecialCharacters(String string){
		string = trimString(string);

		if(string == null)
			return null;
		string = nullIfSpecialCharacters(string);

		if(string.length() == 0)
			return null;
		return string;
	}

	public static boolean checkIfStringsAreValid(String... stringArr)
	{
		for (String str : stringArr)
		{
			if (trimAndnullIfSpecialCharacters(str) == null)
			{
				return false;
			}
		}
		return true;
	}

	public static String asText(JsonNode textNode){
		if(textNode == null || textNode.asText().equals("null"))
			return null;
		return textNode.asText();
	}
}