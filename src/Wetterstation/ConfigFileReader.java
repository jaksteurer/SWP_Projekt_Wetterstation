package Wetterstation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

	public static void main(String[] args) {
		// getConfigFile();

	}

	public String getConfigFile() {
		String path = ".\\configFile\\Konfigurationsdatei.properties";
		String defaultPlace = "";
		try {
			Properties properties = new Properties();
			BufferedInputStream stream;
			stream = new BufferedInputStream(new FileInputStream(path));
			properties.load(stream);
			//Variable mit dem namen "defaultPlace" wird aus der Konfigurationsdatei gelesen
			defaultPlace = properties.getProperty("defaultPlace");
			// System.out.println(defaultPlace + "\t success");
			stream.close();
		} catch (IOException e) {
			System.out.println("[getConfigFile] Fehler: " + e);
			return null;
		}
		return defaultPlace;
	}
}
