import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigData {

	public String getConfigData() {
		String configData = "C:\\Jakob\\Schule HTL Anichstraﬂe\\Jakob Schule 5AHWII (20-21)\\SWP (Rubner)\\Projekt_Wetterstation\\Konfigurationsdatei.properties";
		String defaultPlace = "";
		try {
			Properties properties = new Properties();
			BufferedInputStream stream;
			stream = new BufferedInputStream(new FileInputStream(configData));
			properties.load(stream);
			defaultPlace = properties.getProperty("defaultPlace");
			stream.close();
		} catch (IOException e) {
			System.out.println("[getConfigData]" + e);
			return null;
		}
		return defaultPlace;
	}
}
