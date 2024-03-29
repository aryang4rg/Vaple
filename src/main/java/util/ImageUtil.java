package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil{
	public static String writeToFile(File file, String data) throws IOException{
		String[] base64Image = data.split(",");

		if(base64Image.length < 2)
			return "Invalid Image";
		String image = base64Image[1];
		byte[] imageBytes = Base64.getDecoder().decode(image);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

		if(img == null)
			return "Not a valid image";
		if(img.getWidth() < 32 || img.getHeight() < 32)
			return "Image must be atleast 32x32";

		if(img.getWidth() > 2048 || img.getHeight() > 2048)
			return "Image must be atmost 2048x2048";
		if(imageBytes.length > 1024 * 1024)
			return "Image must be atmost 1024KB";

		if (!file.exists()) {
			file.createNewFile();
		}

		int least = img.getWidth();

		if(img.getHeight() < least)
			least = img.getHeight();
		img = img.getSubimage((img.getWidth() - least) / 2, (img.getHeight() - least) / 2, least, least);

		ImageIO.write(img, "png", file);

		return null;
	}
}