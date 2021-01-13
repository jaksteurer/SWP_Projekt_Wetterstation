package Wetterstation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageDownloader {

	public static void main(String[] args) throws MalformedURLException, IOException {

		// imageDownloaderOW("04d");
		// imageDownloaderWB("r01d");
	}
	public void imageDownloaderOW (String iconId) {
		//Datei wird runtergeladen und im Ordner gespeichert
		//Wenn die Datei bereits vorhanden ist wird sie nicht mehr runtergeladen 
		//und die bereits vorhandene Datei wird verwendet
		BufferedImage image = null;
		try {
			//Pfad zur URL wird erstellt
			URL url = new URL ("http://openweathermap.org/img/w/"+iconId+".png");
			//Url wird ausgelesen
			image = ImageIO.read(url);
			//Datei wird als png format im angegebenen Ordner unter vorgegebenen namen gespeichert
			ImageIO.write(image, "png", new File("./pictures/"+iconId+".png"));
			//System.out.println("success");
		}catch(IOException e) {
			System.out.println("[imageDownloaderOW] Fehler: "+e);
		}
	}
	public void imageDownloaderWB (String iconId) {
		//Datei wird runtergeladen und im Ordner gespeichert
		//Wenn die Datei bereits vorhanden ist wird sie nicht mehr runtergeladen 
		//und die bereits vorhandene Datei wird verwendet
		BufferedImage image = null;
		try {
			//Pfad zur URL wird erstellt
			URL url = new URL ("https://www.weatherbit.io/static/img/icons/"+iconId+".png");
			//Url wird ausgelesen
			image = ImageIO.read(url);
			//Datei wird als png format im angegebenen Ordner unter vorgegebenen namen gespeichert
			ImageIO.write(image, "png", new File("./pictures/"+iconId+".png"));
			//System.out.println("success");
		}catch(IOException e) {
			System.out.println("[imageDownloaderWB] Fehler: "+e);
		}
	}
}
