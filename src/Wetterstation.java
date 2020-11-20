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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.input.DataFormat;

public class Wetterstation {

	static String dGetData = "";
	static String fcGetData = "";
	
	static String temp = "";
	static String temp_max = "";
	static String temp_min = "";
	static String humidity = "";
	static String windspeed = "";
	
	
	static Map<String, Object> dailyWeatherMap;
	static Map<String, Object> dailyWeatherMapWind;
	static String weiter = null;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{
		//Verbindung zur Datenbank herstellen
		SQLConnection connector = new SQLConnection();
		Connection con = connector.getConnection();
		//Konfigurationsdatei
		ConfigData confData = new ConfigData();
		String defPlace = confData.getConfigData();
		
		String place = requestPlace();

//		Scanner scan = new Scanner(System.in);
//		do {
			if (place.equals("d")) {
				place = defPlace;
				System.err.println("Wetter für den Defalut-Ort "+place+":");
				readCurrentWeatherAPI(place);
				readForecastWeatherAPI(place);
				showCurrentWeather();
				showForecastWeather();
				insertToDatabase(con, place);
			}if(place != null && !place.equals("d")) {
				System.err.println("Wetter für den Ort "+place+":");
				readCurrentWeatherAPI(place);
				readForecastWeatherAPI(place);
				showCurrentWeather();
				showForecastWeather();
				insertToDatabase(con, place);
			}else {
				System.out.println("Eingabe überprüfen");
			}
			//Statt den if's nur "Hallo" funktioniert ohne Probleme
			//System.out.println("Hallo");
			
//			System.out.print("Weiteren Ort eingeben? [j/n]: ");
//			weiter = scan.next();
//		}while(weiter.equals("j"));
//		scan.close();

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
			
			
			//ausgabe über Gson
			JsonObject jsondata =  new JsonParser().parse(dGetData).getAsJsonObject();
			//System.out.println(jsonObject);
			temp = jsondata.getAsJsonObject("main").get("temp").getAsString();
			temp_max = jsondata.getAsJsonObject("main").get("temp_max").getAsString();
			temp_min = jsondata.getAsJsonObject("main").get("temp_min").getAsString();
			humidity = jsondata.getAsJsonObject("main").get("humidity").getAsString();
			windspeed = jsondata.getAsJsonObject("wind").get("speed").getAsString();
			System.out.println("Heute: ("+Datum(0)+")");
			System.out.println("\taktuelle Temperatur:\t"+temp+" C°");
			System.out.println("\tmax Temperatur:\t\t"+temp_max+" C°");
			System.out.println("\tmin Temperatur:\t\t"+temp_min+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+humidity+" %");
			System.out.println("\tWind:\t\t\t"+windspeed+" Km/h");
			//ICON id anfordern
			JsonObject weather =  jsondata.getAsJsonArray("weather").get(0).getAsJsonObject();
			//System.out.println("weather: "+weather);
			String iconId = weather.getAsJsonObject().get("icon").getAsString();
			System.out.println("\tICON_ID:\t\t"+iconId);
		
			
			
//			Map<String, Object> main = (Map<String, Object>) curDayMap.get("main");
//			//System.out.println(main);
//			System.out.println("Heute: ("+Datum(0)+")");
//			System.out.println("\taktuelle Temperatur:\t"+main.get("temp")+" C°");
//			System.out.println("\tMinimal Temperatur:\t"+main.get("temp_min")+" C°");
//			System.out.println("\tMaximal Temperatur:\t"+main.get("temp_max")+" C°");
//			System.out.println("\tLuftfeuchtigkeit:\t"+main.get("humidity")+" %");
//			//Wert welcher für Wind benötigt wird steht noch ein einer weiteren Map in der main Map
//			Map<String, Object> wind = (Map<String, Object>) curDayMap.get("wind");
//			System.out.println("\tWind:\t\t\t"+wind.get("speed")+" Km/h");
//			dailyWeatherMapWind = wind;
//			dailyWeatherMap = main;
//			//Foto vom Wetter
//			Map<String, Object> weather = (Map<String, Object>) curDayMap.get("weather");
//			Map<Object, Object> tmp = (Map<Object, Object>) weather.get(0);
//			System.out.println(tmp);
////			System.out.println("Foto_id: "+tmp.get("icon"));
			
			//URL photoURL = new URL("http://openweathermap.org/img/w/"+photo.get("icon")+".png");
			//Test photoday = new Test();
			//Test.getImageOpenweather(photo);
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
			Map<Object, Object> Icon1 = (Map<Object, Object>) data1.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon1.get("icon"));
			//forecast day 2
			Map<Object, Object>data2 = (Map<Object, Object>) data.get(2);
			System.out.println("in 2 Tagen: ("+data2.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data2.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data2.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data2.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data2.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data2.get("pop")+" %");
			Map<Object, Object> Icon2 = (Map<Object, Object>) data2.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon2.get("icon"));
			//forecast day 3
			Map<Object, Object>data3 = (Map<Object, Object>) data.get(3);
			System.out.println("in 3 Tagen: ("+data3.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data3.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data3.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data3.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data3.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data3.get("pop")+" %");
			Map<Object, Object> Icon3 = (Map<Object, Object>) data3.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon3.get("icon"));
			//forecast day 4
			Map<Object, Object>data4 = (Map<Object, Object>) data.get(4);
			System.out.println("in 4 Tagen:("+data4.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data4.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data4.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data4.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data4.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data4.get("pop")+" %");
			Map<Object, Object> Icon4 = (Map<Object, Object>) data4.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon4.get("icon"));
			//forecast day 5
			Map<Object, Object>data5 = (Map<Object, Object>) data.get(5);
			System.out.println("in 5 Tagen:("+data5.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data5.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data5.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data5.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data5.get("wind_spd")*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
			Map<Object, Object> Icon5 = (Map<Object, Object>) data5.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon5.get("icon"));
			
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
			stm.setString(2, temp_max);//(double) dailyWeatherMap.get("temp_max"));
			stm.setString(3, temp_min);//(double) dailyWeatherMap.get("temp_min"));
			stm.setString(4, humidity);//(double) dailyWeatherMap.get("humidity"));
			stm.setString(5, windspeed);//(double) dailyWeatherMapWind.get("speed"));
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
