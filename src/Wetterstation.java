import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
//Properties
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;

import com.google.gson.Gson;

import javafx.scene.input.DataFormat;

public class Wetterstation {

	static String dGetData = "";
	static String fcGetData = "";
	static Map<String, Object> dailyWeatherMap;
	static Map<String, Object> dailyWeatherMapWind;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{
		//Verbindung zur Datenbank herstellen
		SQLConnection connector = new SQLConnection();
		Connection con = connector.getConnection();

		//Ort eingeben lassen
		String defPlace = getConfigData();
		String place = requestPlace();
		if (place.equals("d")) {
			place = defPlace;
			System.err.println("Wetter für den Ort "+place+":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather();
			showForecastWeather();
			insertToDatabase(con, place);
		}if(place !=null) {
			System.err.println("Wetter für den Ort "+place+":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather();
			showForecastWeather();
			insertToDatabase(con, place);
		}
		connector.releaseConnection(con);
	}
	public static String Datum(int datum) {
		//Instanz vom Typ Kalender wird erstellt
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		//5 Tage in die Zukunft in Variable speichern
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date TwoDaysFc = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date ThreeDaysFc = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date FourDaysFc = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date FiveDaysFc = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//Ausgabe
		switch(datum) {
		case 0: return dateFormat.format(now);  		//today Datum(0);
		case 1: return dateFormat.format(tomorrow); 	//Tomorrow Datum(1);
		case 2: return dateFormat.format(TwoDaysFc); 	//2 days forecast Datum(2);
		case 3: return dateFormat.format(ThreeDaysFc);	//3 days forecast Datum(3);
		case 4: return dateFormat.format(FourDaysFc);	//4 days forecast Datum(4);
		case 5: return dateFormat.format(FiveDaysFc);	//5 days forecast Datum(5);
		default: System.out.println("Wert muss zwischen 0 und 5 liegen");
		}
		return "Wert muss zwischen 0 und 5 liegen";
	}
	@SuppressWarnings("unchecked")
	public static void showCurrentWeather() {
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			Map<String, Object> curDayMap = new Gson().fromJson(dGetData, Map.class);
			/*
			 * Java Map ausgeben mit allen Werten und den Keys
			 * 		for (Map.Entry<String, Object> key : dailyWeatherMap.entrySet()) {
			 * 			System.out.println(key.getKey()+"="+key.getValue());
			 * 		}
			 * 
			 * Werte eines einzelnen Keys anzeigen lassen
			 * 		System.out.println("Main: "+ dailyWeatherMap.get("main"));
			 * 
			 * Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			 * und davon wieder Map.get("Objektname") herausfiltern um genau den
			 * gewünschten Wert zu bekommen
			 */
			Map<String, Object> main = (Map<String, Object>) curDayMap.get("main");
			System.out.println("Heute: ("+Datum(0)+")");
			System.out.println("\taktuelle Temperatur:\t"+main.get("temp")+" C°");
			System.out.println("\tMinimal Temperatur:\t"+main.get("temp_min")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+main.get("temp_max")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+main.get("humidity")+" %");
			//Wert welcher für Wind benötigt wird steht noch ein einer weiteren Map in der main Map
			Map<String, Object> wind = (Map<String, Object>) curDayMap.get("wind");
			System.out.println("\tWind:\t\t\t"+wind.get("speed")+" Km/h");
			dailyWeatherMapWind = wind;
			dailyWeatherMap = main;
		}catch (NullPointerException e){
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[showCurrenttWeather]Sinnvollen Ort eingeben");
			 */
		}catch(Exception e) {
			System.out.println("[showCurrenttWeather]Fehler liegt nicht am Ort! Fehler: "+e);
		}
	}
	@SuppressWarnings("unchecked")
	public static void showForecastWeather() {
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			Map<String, Object> foreDayMap = new Gson().fromJson(fcGetData, Map.class);
			/*
			 * Java Map ausgeben mit allen Werten und den Keys
			 *		for (Map.Entry<String, Object> key : forecastWeatherMap.entrySet()) {
			 *					System.out.println(key.getKey()+"="+key.getValue());
			 *		}
			 * Werte eines einzelnen Keys anzeigen lassen
			 * System.out.println("data: "+ forecastWeatherMap.get("data"));
			 * 
			 *  Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			 *  und davon wieder Map.get("Objektname") herausfiltern um genau den
			 *  gewünschten Wert zu bekommen.
			 */
			List<Object> data = (List<Object>) foreDayMap.get("data");
			//System.out.println("data: "+data);
			//today
			/*
			Map<Object, Object>data0 = (Map<Object, Object>) data.get(0);
			 System.out.println("heute: ("+data0.get("datetime")+")");
			 System.out.println("\tMinimal Temperatur:\t"+data0.get("low_temp")+" C°");
			 System.out.println("\tMaximal Temperatur:\t"+data0.get("max_temp")+" C°");
			 System.out.println("\tLuftfeuchtigkeit:\t"+data0.get("rh")+" %");
			 System.out.println("\tNiederschlag:\t\t"+data0.get("pop")+" %");
			 */
			//forecast day 1
			Map<Object, Object>data1 = (Map<Object, Object>) data.get(1);
			System.out.println("Morgen: ("+data1.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data1.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data1.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data1.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data1.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data1.get("pop")+" %");
			//forecast day 2
			Map<Object, Object>data2 = (Map<Object, Object>) data.get(2);
			System.out.println("in 2 Tagen: ("+data2.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data2.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data2.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data2.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data2.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data2.get("pop")+" %");
			//forecast day 3
			Map<Object, Object>data3 = (Map<Object, Object>) data.get(3);
			System.out.println("in 3 Tagen: ("+data3.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data3.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data3.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data3.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data3.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data3.get("pop")+" %");
			//forecast day 4
			Map<Object, Object>data4 = (Map<Object, Object>) data.get(4);
			System.out.println("in 4 Tagen:("+data4.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data4.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data4.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data4.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data4.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data4.get("pop")+" %");
			//forecast day 5
			Map<Object, Object>data5 = (Map<Object, Object>) data.get(5);
			System.out.println("in 5 Tagen:("+data5.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data5.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data5.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data5.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data5.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
		}catch (NullPointerException e){
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[forecasttWeather]Sinnvollen Ort eingeben");
			 */
		}catch(Exception e) {
			System.out.println("[forecasttWeather]Fehler liegt nicht am Ort! Fehler: "+e);
		}
	}
	public static String requestPlace() {
		String place=null;
		try {	
			Scanner sc = new Scanner(System.in);
			System.out.print("Ort in Österreich eingeben: ");
			place = sc.next();
			sc.close();
		}catch (NullPointerException e) {
			System.out.println("existierenden Ort eingeben");
			return null;
		}catch (Exception e) {
			System.out.println("[requestPlace]Fehler liegt nicht am Ort! Fehler: "+e);
		}
		return place;
	}
	public static String getConfigData() {
		String configData = "C:\\Jakob\\Schule HTL Anichstraße\\Jakob Schule 5AHWII (20-21)\\SWP (Rubner)\\Projekt_Wetterstation\\Konfigurationsdatei.properties";
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
	public static void readCurrentWeatherAPI(String place) {
		//save current day json request in String
		URL dUrl;
		try {
			dUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q="+place+",at&units=metric&appid=793d753c4a6623defbfafdce3d337e9b");
			HttpURLConnection dConn = (HttpURLConnection)dUrl.openConnection();
			dConn.setRequestMethod("GET");
			dConn.connect();
			int dResponsecode = dConn.getResponseCode();
			//System.out.println("Code: "+dResponsecode);
			if(dResponsecode != 200) {
				System.out.println("Bitte sinnvollen Ort eingeben!"); 
				//System.out.println("[readCurrentWeatherAPI]HttpResonseCode: "+dResponsecode);
				//System.out.println("Help for responsecodes: https://www.tutorialspoint.com/servlets/servlets-http-status-codes.htm");
			}else {
				Scanner sc = new Scanner(dUrl.openStream());
				while(sc.hasNext()) {
					dGetData +=sc.nextLine();
				}
				//System.out.println(dInline);
				sc.close();
			}
		} catch (IOException e) {
			System.out.println("[readCurrentWeatherAPI] ungeklärter Fehler beim Einlesen der Current Weather API: "+e);
		}
	}
	public static void readForecastWeatherAPI(String place) {
		//save forecast weather json request in String
		URL fcUrl;
		try {
			fcUrl = new URL("https://api.weatherbit.io/v2.0/forecast/daily?city="+place+"&country=at&days=6&lang=de&key=1c3d135c16f640c1823ce502f303e586");
			HttpURLConnection fcConn = (HttpURLConnection)fcUrl.openConnection();
			fcConn.setRequestMethod("GET");
			fcConn.connect();
			int fcResponsecode = fcConn.getResponseCode();
			//System.out.println("Code: "+fcResponsecode);
			if(fcResponsecode != 200) {
				/*
				 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
				 * System.out.println("Bitte sinnvollen Ort eingeben!");
				 * System.out.println("[readForecastWeatherAPI]HttpResonseCode: "+fcResponsecode);
				 * System.out.println("Help for responsecodes: https://www.tutorialspoint.com/servlets/servlets-http-status-codes.htm");
				 */
			}else {
				Scanner sc = new Scanner(fcUrl.openStream());
				while(sc.hasNext()) {
					fcGetData +=sc.nextLine();
				}
				sc.close();
			}
		}catch(IOException e) {
			System.out.println("[readForecastWeatherAPI] ungeklärter Fehler beim Einlesen der Forecast Weather API: "+e);
		}
	}
	public static void insertToDatabase(Connection con, String place)
			throws SQLException {
		String sql = "INSERT INTO temperatures values (NOW(),?,?,?,?,?)"
				+ "	ON DUPLICATE KEY" + 
				"	UPDATE maxtempInCelsius = VALUES(maxtempInCelsius)," + 
				"	mintempInCelsius = VALUES(mintempInCelsius)," + 
				"	humidityInPercent = VALUES(humidityInPercent)," + 
				"	windInKilometerPerHour = VALUES(windInKilometerPerHour);";
		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement(sql);
			stm.setString(1, place);
			stm.setDouble(2, (double) dailyWeatherMap.get("temp_max"));
			stm.setDouble(3, (double) dailyWeatherMap.get("temp_min"));
			stm.setDouble(4, (double) dailyWeatherMap.get("humidity"));
			stm.setDouble(5, (double) dailyWeatherMapWind.get("speed"));
			stm.executeUpdate();
		}catch(NullPointerException e) {
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[insertToDatabase] Sinnvollen Ort eingeben");
			 */
		}catch(Exception e) {
			System.out.println("[insertToDatabase] Fehler liegt nicht am Ort! Fehler:"+e);
		}
		finally {
			if (stm != null)
				stm.close();
		}

	}




}
