import java.io.IOException;
import java.net.HttpURLConnection;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Wetterstation {

	static String dGetData = "";
	static String fcGetData = "";

	static double tempOW ;
	static double temp_maxOW;
	static double temp_minOW ;
	static double humidityOW ;
	static double windspeedOW ;

	static double tempWB ;
	static double max_tempWB;
	static double low_tempWB;
	static double rhWB ;
	static double wind_spdWB ;
	static String iconWB;

	static double tempAV ;
	static double max_tempAV;
	static double low_tempAV;
	static double humidityAV ;
	static double windspeedAV ;

	static String weiter = null;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{
		//Verbindung zur DB herstellen
		SQLConnection connector = new SQLConnection();
		Connection con = connector.getConnection();

		//Konfigurationsdatei
		ConfigFileReader confData = new ConfigFileReader();
		String defPlace = confData.getConfigFile();

		//Ort abfragen
		String place = requestPlace();

		//		Scanner scan = new Scanner(System.in);
		//		do {
		if (place.equals("d")) {
			place = defPlace;
			System.err.println("Wetter für den Defalut-Ort "+place+":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather(place);
			showForecastWeather(place);
			insertToDatabase(con, place);
		}if(place != null && !place.equals("d")) {
			System.err.println("Wetter für den Ort "+place+":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather(place);
			showForecastWeather(place);
			//insertToDatabase(con, place);
		}else {
			System.out.println("Eingabe überprüfen");
		}
		//Statt den if's nur "Hallo" funktioniert ohne Probleme
		//System.out.println("Hallo");

		//			System.out.print("Weiteren Ort eingeben? [j/n]: ");
		//			weiter = scan.next();
		//		}while(weiter.equals("j"));
		//		scan.close();

		//Verbindung zur DB trennen
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
	public static void showCurrentWeather(String place) {
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			//Map<String, Object> curDayMap = new Gson().fromJson(dGetData, Map.class);
			/*
			 * Java Map ausgeben mit allen Werten und den Keys:
			 * 		for (Map.Entry<String, Object> key : curDayMap.entrySet()) {
			 * 			System.out.println(key.getKey()+"="+key.getValue());
			 * 		}
			 * 
			 * Werte eines einzelnen Keys anzeigen lassen
			 * 		System.out.println("Main: "+ curDayMap.get("main"));
			 * 
			 * Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			 * und davon wieder Map.get("Objektname") herausfiltern um genau den
			 * gewünschten Wert zu bekommen
			 */

			//Diese Methode belegt Werte von anderer API für Durchschnittsberechnung
			showForecastWeatherToday(place);

			//Daten Openweathermap ausgabe über Gson
			JsonObject jsondata =  new JsonParser().parse(dGetData).getAsJsonObject();
			//System.out.println(jsonObject);
			tempOW = jsondata.getAsJsonObject("main").get("temp").getAsDouble();
			temp_maxOW = jsondata.getAsJsonObject("main").get("temp_max").getAsDouble();
			temp_minOW = jsondata.getAsJsonObject("main").get("temp_min").getAsDouble();
			humidityOW = jsondata.getAsJsonObject("main").get("humidity").getAsDouble();
			windspeedOW = jsondata.getAsJsonObject("wind").get("speed").getAsDouble();

			//Durchschnittswerte Berechnen (Zusatzaufgabe)
			tempAV = Math.round((tempOW+tempWB)/2*100)/100.;
			max_tempAV =Math.round((temp_maxOW+max_tempWB)/2*100)/100.;
			low_tempAV =Math.round((temp_minOW+low_tempWB)/2*100)/100.;
			humidityAV =Math.round((humidityOW+rhWB)/2*100)/100.;
			windspeedAV =Math.round((windspeedOW+wind_spdWB)/2*100)/100.;

			//Ausgabe in der Console
			System.out.println("Heute: ("+Datum(0)+")");
			System.out.println("\t\t\t\topenweather.com\t\tWeatherbit.io\t\tDurchschnitt");
			System.out.println("\taktuelle Temperatur:\t"+tempOW+" C°  \t\t"+tempWB+" C°  \t\t"+tempAV+" C°"); 
			System.out.println("\tMax Temperatur:\t\t"+temp_maxOW+" C°  \t\t"+max_tempWB+" C°  \t\t"+max_tempAV+" C°");
			System.out.println("\tMin Temperatur:\t\t"+temp_minOW+" C°  \t\t"+low_tempWB+" C°  \t\t"+low_tempAV+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+humidityOW+" %  \t\t"+rhWB+" %  \t\t"+humidityAV+" %");
			System.out.println("\tWind:\t\t\t"+windspeedOW+" Km/h  \t\t"+wind_spdWB+" Km/h  \t\t"+windspeedAV+" Km/h");

			//ICON id anfordern
			JsonObject weather =  jsondata.getAsJsonArray("weather").get(0).getAsJsonObject();
			//System.out.println("weather: "+weather);
			String iconId = weather.getAsJsonObject().get("icon").getAsString();
			System.out.println("\tICON_ID:\t\t  "+iconId+"\t\t\t"+iconWB);

			//ImageDownloader
			ImageDownloader today = new ImageDownloader();
			today.imageDownloaderOW(iconId, Datum(0)+"_"+place);
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
	public static void showForecastWeather(String place) {
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			Map<String, Object> foreDayMap = new Gson().fromJson(fcGetData, Map.class);
			/*
			 * Java Map ausgeben mit allen Werten und den Keys
			 *		for (Map.Entry<String, Object> key : foreDayMap.entrySet()) {
			 *					System.out.println(key.getKey()+"="+key.getValue());
			 *		}
			 * Werte eines einzelnen Keys anzeigen lassen
			 * System.out.println("data: "+ foreDayMap.get("data"));
			 * 
			 *  Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			 *  und davon wieder Map.get("Objektname") herausfiltern um genau den
			 *  gewünschten Wert zu bekommen.
			 */
			List<Object> data = (List<Object>) foreDayMap.get("data");
			//System.out.println("data: "+data);

			//forecast day 1
			Map<String, Object>data1 = (Map<String, Object>) data.get(1);
			System.out.println("Morgen: ("+data1.get("datetime")+")");
			System.out.println("\tMax Temperatur:\t\t"+data1.get("max_temp")+" C°");
			System.out.println("\tMin Temperatur:\t\t"+data1.get("low_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data1.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data1.get("wind_spd")*100)/100.+" Km/h");
			//System.out.println("\tNiederschlag:\t\t"+data1.get("pop")+" %");
			Map<String, Object> Icon1 = (Map<String, Object>) data1.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon1.get("icon"));
			//ImageDownloader
			ImageDownloader tomorrow = new ImageDownloader();
			tomorrow.imageDownloaderWB(Icon1.get("icon").toString(), data1.get("datetime").toString()+"_"+place);

			//forecast day 2
			Map<Object, Object>data2 = (Map<Object, Object>) data.get(2);
			System.out.println("in 2 Tagen: ("+data2.get("datetime")+")");
			System.out.println("\tMax Temperatur:\t\t"+data2.get("max_temp")+" C°");
			System.out.println("\tMin Temperatur:\t\t"+data2.get("low_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data2.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data2.get("wind_spd")*100)/100.+" Km/h");
			//System.out.println("\tNiederschlag:\t\t"+data2.get("pop")+" %");
			Map<Object, Object> Icon2 = (Map<Object, Object>) data2.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon2.get("icon"));
			//ImageDownloader
			ImageDownloader two = new ImageDownloader();
			two.imageDownloaderWB(Icon2.get("icon").toString(), data2.get("datetime").toString()+"_"+place);

			//forecast day 3
			Map<Object, Object>data3 = (Map<Object, Object>) data.get(3);
			System.out.println("in 3 Tagen: ("+data3.get("datetime")+")");
			System.out.println("\tMax Temperatur:\t\t"+data3.get("max_temp")+" C°");
			System.out.println("\tMin Temperatur:\t\t"+data3.get("low_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data3.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data3.get("wind_spd")*100)/100.+" Km/h");
			//System.out.println("\tNiederschlag:\t\t"+data3.get("pop")+" %");
			Map<Object, Object> Icon3 = (Map<Object, Object>) data3.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon3.get("icon"));
			//ImageDownloader
			ImageDownloader three = new ImageDownloader();
			three.imageDownloaderWB(Icon3.get("icon").toString(), data3.get("datetime").toString()+"_"+place);

			//forecast day 4
			Map<Object, Object>data4 = (Map<Object, Object>) data.get(4);
			System.out.println("in 4 Tagen:("+data4.get("datetime")+")");
			System.out.println("\tMax Temperatur:\t\t"+data4.get("max_temp")+" C°");
			System.out.println("\tMin Temperatur:\t\t"+data4.get("low_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data4.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data4.get("wind_spd")*100)/100.+" Km/h");
			//System.out.println("\tNiederschlag:\t\t"+data4.get("pop")+" %");
			Map<Object, Object> Icon4 = (Map<Object, Object>) data4.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon4.get("icon"));
			//ImageDownloader
			ImageDownloader four = new ImageDownloader();
			four.imageDownloaderWB(Icon4.get("icon").toString(), data4.get("datetime").toString()+"_"+place);

			//forecast day 5
			Map<Object, Object>data5 = (Map<Object, Object>) data.get(5);
			System.out.println("in 5 Tagen:("+data5.get("datetime")+")");
			System.out.println("\tMax Temperatur:\t\t"+data5.get("max_temp")+" C°");
			System.out.println("\tMin Temperatur:\t\t"+data5.get("low_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data5.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round((double) data5.get("wind_spd")*100)/100.+" Km/h");
			//System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
			Map<Object, Object> Icon5 = (Map<Object, Object>) data5.get("weather");
			System.out.println("\tICON-ID:\t\t"+Icon5.get("icon"));
			//ImageDownloader
			ImageDownloader five = new ImageDownloader();
			five.imageDownloaderWB(Icon5.get("icon").toString(), data5.get("datetime").toString()+"_"+place);

		}catch (NullPointerException e){
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[forecasttWeather]Sinnvollen Ort eingeben");
			 */
		}catch(Exception e) {
			System.out.println("[forecasttWeather]Fehler liegt nicht am Ort! Fehler: "+e);
		}
	}
	@SuppressWarnings("unchecked")
	public static void showForecastWeatherToday(String place) {
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

		Map<Object, Object>data0 = (Map<Object, Object>) data.get(0);
		//System.out.println("heute: ("+data0.get("datetime")+")");
		tempWB=(double) data0.get("temp");									//aktuelle Temp
		max_tempWB = (double) data0.get("max_temp");						//maximal Temp
		low_tempWB = (double) data0.get("low_temp");						//minimal Temp
		rhWB = (double)data0.get("rh");										//Luftfeuchtigkeit
		wind_spdWB = Math.round((double) data0.get("wind_spd")*100)/100.;	//Wind
		Map<String,Object>Icon0 = (Map<String, Object>)data0.get("weather");//Icon
		iconWB = Icon0.get("icon").toString();
		//ImageDownloader
		ImageDownloader today = new ImageDownloader();
		today.imageDownloaderWB(Icon0.get("icon").toString(), data0.get("datetime").toString()+"_"+place);
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
			stm.setDouble(2, max_tempAV);
			stm.setDouble(3, low_tempAV);
			stm.setDouble(4, humidityAV);
			stm.setDouble(5, windspeedAV);
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
