import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigData {

	public static void main(String[] args) {
		//getConfigData();
		
	}
	public String getConfigData() {
		String path = ".\\Konfigurationsdatei.properties";
		String defaultPlace = "";
		try {
			Properties properties = new Properties();
			BufferedInputStream stream;
			stream = new BufferedInputStream(new FileInputStream(path));
			properties.load(stream);
			defaultPlace = properties.getProperty("defaultPlace");
			//System.out.println(defaultPlace + "\t success");
			stream.close();
		} catch (IOException e) {
			System.out.println("[getConfigData]" + e);
			return null;
		}
		return defaultPlace;
	}
}
