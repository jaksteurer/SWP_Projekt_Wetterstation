import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageDownloader {

	public static void main(String[] args) throws MalformedURLException, IOException {

		//imageDownloaderOW("04d", "ziege");
		//imageDownloaderWB("r01d", "huhn");
	}
	public void imageDownloaderOW (String iconId, String name) {
		BufferedImage image = null;
		try {
			URL url = new URL ("http://openweathermap.org/img/w/"+iconId+".png");
			image = ImageIO.read(url);
			ImageIO.write(image, "png", new File(".\\pictures\\"+name+"_OW.png"));
			//System.out.println("success");
		}catch(IOException e) {
			System.out.println("[imageDownloaderOW] Fehler: "+e);
		}
	}
	public void imageDownloaderWB (String iconId, String name) {
		BufferedImage image = null;
		try {
			URL url = new URL ("https://www.weatherbit.io/static/img/icons/"+iconId+".png");
			image = ImageIO.read(url);
			ImageIO.write(image, "png", new File(".\\pictures\\"+name+"_WB.png"));
			//System.out.println("success");
		}catch(IOException e) {
			System.out.println("[imageDownloaderWB] Fehler: "+e);
		}
	}

}

