package Wetterstation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Wetterstation {

	static String dGetData = "";
	static Reader dGetDataR;
	static String fcGetData = "";
	static Reader fcGetDataR;
	
	static double tempOW;
	static double temp_maxOW;
	static double temp_minOW;
	static double humidityOW;
	static double windspeedOW;
	

	static double tempWB0;
	static double max_tempWB0;
	static double low_tempWB0;
	static double rhWB0;
	static double wind_spdWB0;
	//public weil sie in der Klasse MainController verwendet werden
	public static double precipitationWB;
	public static double corrFact;
	public static String iconWB0;
	public static String iconWB1;
	public static String iconWB2;
	public static String iconWB3;
	public static String iconWB4;
	public static String iconWB5;
	public static double max_tempWB1;
	public static double low_tempWB1;
	public static double max_tempWB2;
	public static double low_tempWB2;
	public static double max_tempWB3;
	public static double low_tempWB3;
	public static double max_tempWB4;
	public static double low_tempWB4;
	public static double max_tempWB5;
	public static double low_tempWB5;
	public static double humidityAV;
	public static double windspeedAV;
	//public weil sie in der Klasse MainController verwendet werden
	public static  double tempAV;
	public static double max_tempAV;
	public static  double low_tempAV;

	static String weiter = null;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
				
		// Verbindung zur DB herstellen
		SQLConnection connector = new SQLConnection();
		Connection con = connector.getConnection();

		// Konfigurationsdatei
		ConfigFileReader confData = new ConfigFileReader();
		String defPlace = confData.getConfigFile();

		// Ort abfragen
		String place = requestPlace();

		// Scanner scan = new Scanner(System.in);
		// do {
		if (place.equals("d")) {
			place = defPlace;
			System.err.println("Wetter für den Defalut-Ort " + place + ":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather(place);
			showForecastWeather(place);
			insertToDatabase(con, place);
		}
		if (place != null && !place.equals("d")) {
			System.err.println("Wetter für den Ort " + place + ":");
			readCurrentWeatherAPI(place);
			readForecastWeatherAPI(place);
			showCurrentWeather(place);
			showForecastWeather(place);
			// insertToDatabase(con, place);
		} else {
			System.out.println("Eingabe überprüfen");
		}
		// Statt den if's nur "Hallo" funktioniert ohne Probleme
		// System.out.println("Hallo");

		// System.out.print("Weiteren Ort eingeben? [j/n]: ");
		// weiter = scan.next();
		// }while(weiter.equals("j"));
		// scan.close();

		// Verbindung zur DB trennen
		//connector.releaseConnection(con);
		 
	}
	public static String Datum(int datum) {
		// Instanz vom Typ Kalender wird erstellt
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		// 5 Tage in die Zukunft in Variable speichern
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

		// Ausgabe
		switch (datum) {
		case 0:
			return dateFormat.format(now); // today Datum(0);
		case 1:
			return dateFormat.format(tomorrow); // Tomorrow Datum(1);
		case 2:
			return dateFormat.format(TwoDaysFc); // 2 days forecast Datum(2);
		case 3:
			return dateFormat.format(ThreeDaysFc); // 3 days forecast Datum(3);
		case 4:
			return dateFormat.format(FourDaysFc); // 4 days forecast Datum(4);
		case 5:
			return dateFormat.format(FiveDaysFc); // 5 days forecast Datum(5);
		default:
			System.out.println("Wert muss zwischen 0 und 5 liegen");
		}
		return "Wert muss zwischen 0 und 5 liegen";
	}
	public static void showCurrentWeather(String place) {
		try {

			// Diese Methode belegt Werte von anderer API (WB) für Durchschnittsberechnung
			showForecastWeatherToday(place);

			// Daten Openweathermap ausgabe über Gson
			JsonReader reader = new JsonReader(new StringReader(dGetData));
			reader.setLenient(true);
			JsonObject jsondataCurrent = (JsonObject) new JsonParser().parse(reader);
				//Alternative
				//JsonObject jsondataCurrent = new JsonParser().parse(dGetData).getAsJsonObject();
			
			// System.out.println(jsonObject);
			tempOW 		= jsondataCurrent.getAsJsonObject("main").get("temp").getAsDouble();
			temp_maxOW 	= jsondataCurrent.getAsJsonObject("main").get("temp_max").getAsDouble();
			temp_minOW 	= jsondataCurrent.getAsJsonObject("main").get("temp_min").getAsDouble();
			humidityOW 	= jsondataCurrent.getAsJsonObject("main").get("humidity").getAsDouble();
			windspeedOW = jsondataCurrent.getAsJsonObject("wind").get("speed").getAsDouble();

			//(BONUS) Durchschnittswerte Berechnen
			tempAV 		= Math.round((tempOW+tempWB0)/2*100)/100.;
			max_tempAV 	= Math.round((temp_maxOW+max_tempWB0)/2*100)/100.;
			low_tempAV 	= Math.round((temp_minOW+low_tempWB0)/2*100)/100.;
			humidityAV 	= Math.round((humidityOW+rhWB0)/2*100)/100.;
			windspeedAV = Math.round((windspeedOW+wind_spdWB0)/2*100)/100.;

			//(BONUS) Korrekturfaktor
			corrFact = randomDoubleGenerator(low_tempAV, max_tempAV);

			//Ausgabe in der Console
			System.out.println("Heute: ("+Datum(0)+")");
			System.out.println("\t\t\t\topenweather.com\t\tWeatherbit.io\t\tDurchschnitt");
			System.out.println("\taktuelle Temperatur:\t"+tempOW+" C°  \t\t"+tempWB0+" C°  \t\t"+tempAV+" C°");
			System.out.println("\tKorrekturfaktor:\t\t\t\t\t\t\t"+corrFact+" C°");
			System.out.println("\tMax Temperatur:\t\t"+temp_maxOW+" C°  \t\t"+max_tempWB0+" C°  \t\t"+max_tempAV+" C°");
			System.out.println("\tMin Temperatur:\t\t"+temp_minOW+" C°  \t\t"+low_tempWB0+" C°  \t\t"+low_tempAV+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+humidityOW+" %  \t\t"+rhWB0+" %  \t\t"+humidityAV+" %");
			System.out.println("\tNiederschlag:\t\t\t\t\t\t\t\t"+precipitationWB+" %  ");
			System.out.println("\tWind:\t\t\t"+windspeedOW+" Km/h  \t\t"+wind_spdWB0+" Km/h  \t\t"+windspeedAV+" Km/h");

			// ICON id anfordern
			JsonObject weather = jsondataCurrent.getAsJsonArray("weather").get(0).getAsJsonObject();
			// System.out.println("weather: "+weather);
			String iconId = weather.getAsJsonObject().get("icon").getAsString();
			System.out.println("\tICON_ID:\t\t"+iconId+"\t\t\t"+iconWB0);

			// ImageDownloader
			ImageDownloader today = new ImageDownloader();
			today.imageDownloaderOW(iconId);
		} catch (NullPointerException e) {
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[showCurrenttWeather]Sinnvollen Ort eingeben");
			 */
		} 
		catch (Exception e) {
			e.printStackTrace();
			//System.out.println("[showCurrenttWeather]Fehler liegt nicht am Ort! Fehler: " + e);
		}
	}
	public static void showForecastWeather(String place) {
		try {		
			// Daten Weatherbit.io Ausgabe über Gson
			JsonReader reader = new JsonReader(new StringReader(fcGetData));
			reader.setLenient(true);
			JsonObject jsondataForecast = (JsonObject) new JsonParser().parse(reader);
				//Alternative
				//JsonObject jsondataForecast = new JsonParser().parse(fcGetData).getAsJsonObject();
			
			//System.out.println("Forecast: "+jsondataForecast); //Funktioniert

			//Muss in Array umgewandelt werden weil  "data" aus einem Array besteht
			JsonArray data = jsondataForecast.getAsJsonArray("data");
			//System.out.println("data: "+data); //Funktioniert

			//Aus dem Array wird die jeweilige Stelle verwendet welche den Tag darstellt
			JsonObject data1 = (JsonObject) data.getAsJsonArray().get(1);
			JsonObject data2 = (JsonObject) data.getAsJsonArray().get(2);
			JsonObject data3 = (JsonObject) data.getAsJsonArray().get(3);
			JsonObject data4 = (JsonObject) data.getAsJsonArray().get(4);
			JsonObject data5 = (JsonObject) data.getAsJsonArray().get(5);
			//System.out.println("data1: "+data1); //Funktioniert

			//Ein wert aus dem Array an Stelle 0 testen
			//System.out.println("wert 1: "+data1.get("max_temp")); //Funktioniert

			max_tempWB1 = data1.get("max_temp").getAsDouble();
			low_tempWB1 = data1.get("low_temp").getAsDouble();
			max_tempWB2 = data2.get("max_temp").getAsDouble();
			low_tempWB2 = data2.get("low_temp").getAsDouble();
			max_tempWB3 = data3.get("max_temp").getAsDouble();
			low_tempWB3 = data3.get("low_temp").getAsDouble();
			max_tempWB4 = data4.get("max_temp").getAsDouble();
			low_tempWB4 = data4.get("low_temp").getAsDouble();
			max_tempWB5 = data5.get("max_temp").getAsDouble();
			low_tempWB5 = data5.get("low_temp").getAsDouble();

			//forecast day 1
			System.out.println("Morgen: ("+Datum(1)+")"); 	//Alternative zum Datum: data1.get("datetime");
			System.out.println("\tMax Temperatur:\t\t"+max_tempWB1+" C°");
			System.out.println("\tMin Temperatur:\t\t"+low_tempWB1+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data1.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round(/*(double)*/data1.get("wind_spd").getAsDouble()*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data1.get("pop")+" %");
			//Image
			JsonObject Icon1 =  (JsonObject) data1.get("weather");
			iconWB1 = Icon1.get("icon").getAsString();
			System.out.println("\tICON-ID:\t\t"+iconWB1);
			//ImageDownloader
			ImageDownloader tomorrow = new ImageDownloader();
			tomorrow.imageDownloaderWB(iconWB1);

			//forecast day 2
			System.out.println("in 2 Tagen: ("+Datum(2)+")");	//Alternative zum Datum: data2.get("datetime");
			System.out.println("\tMax Temperatur:\t\t"+max_tempWB2+" C°");
			System.out.println("\tMin Temperatur:\t\t"+low_tempWB2+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data2.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round(/*(double)*/ data2.get("wind_spd").getAsDouble()*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data2.get("pop")+" %");
			//Image
			JsonObject Icon2 =  (JsonObject) data2.get("weather");
			iconWB2 = Icon2.get("icon").getAsString();
			System.out.println("\tICON-ID:\t\t"+iconWB2);
			//ImageDownloader
			ImageDownloader two = new ImageDownloader();
			two.imageDownloaderWB(iconWB2);

			//forecast day 3
			System.out.println("in 3 Tagen: ("+Datum(3)+")");	//Alternative zum Datum: data3.get("datetime");
			System.out.println("\tMax Temperatur:\t\t"+max_tempWB3+" C°");
			System.out.println("\tMin Temperatur:\t\t"+low_tempWB3+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data3.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round(/*(double)*/ data3.get("wind_spd").getAsDouble()*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data3.get("pop")+" %");
			//Image
			JsonObject Icon3 =  (JsonObject) data3.get("weather");
			iconWB3 = Icon3.get("icon").getAsString();
			System.out.println("\tICON-ID:\t\t"+iconWB3);
			//ImageDownloader
			ImageDownloader three = new ImageDownloader();
			three.imageDownloaderWB(iconWB3);

			//forecast day 4
			System.out.println("in 4 Tagen:("+Datum(4)+")");	//Alternative zum Datum: data4.get("datetime");
			System.out.println("\tMax Temperatur:\t\t"+max_tempWB4+" C°");
			System.out.println("\tMin Temperatur:\t\t"+low_tempWB4+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data4.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round(/*(double)*/ data4.get("wind_spd").getAsDouble()*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data4.get("pop")+" %");
			//Image
			JsonObject Icon4 =  (JsonObject) data4.get("weather");
			iconWB4 = Icon4.get("icon").getAsString();
			System.out.println("\tICON-ID:\t\t"+iconWB4);
			//ImageDownloader
			ImageDownloader four = new ImageDownloader();
			four.imageDownloaderWB(iconWB4);

			//forecast day 5
			System.out.println("in 5 Tagen:("+Datum(5)+")");	//Alternative zum Datum: data5.get("datetime");
			System.out.println("\tMax Temperatur:\t\t"+max_tempWB5+" C°");
			System.out.println("\tMin Temperatur:\t\t"+low_tempWB5+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data5.get("rh")+" %");
			System.out.println("\tWind:\t\t\t"+Math.round(/*(double)*/ data5.get("wind_spd").getAsDouble()*100)/100.+" Km/h");
			System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
			//Image
			JsonObject Icon5 =  (JsonObject) data5.get("weather"); 
			iconWB5 = Icon5.get("icon").getAsString();
			System.out.println("\tICON-ID:\t\t"+iconWB5);
			//ImageDownloader
			ImageDownloader five = new ImageDownloader();
			five.imageDownloaderWB(iconWB5);

		} catch (NullPointerException e) {
			/*
			 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese Ausgabe erscheint
			 * System.out.println("[forecasttWeather]Sinnvollen Ort eingeben");
			 */
		} catch (Exception e) {
			System.out.println("[showForecastWeather]Fehler liegt nicht am Ort! Fehler: " + e);
		}
	}
	public static void showForecastWeatherToday(String place) {
		
		JsonReader reader = new JsonReader(new StringReader(fcGetData));
		reader.setLenient(true);
		JsonObject jsondataForecast = (JsonObject) new JsonParser().parse(reader);
			//Alternative
			//JsonObject jsondataForecast = new JsonParser().parse(fcGetData).getAsJsonObject();

		//Muss in Array umgewandelt werden weil  "data" aus einem Array besteht
		JsonArray data = jsondataForecast.getAsJsonArray("data");
		//System.out.println("data: "+data); //Funktioniert

		//Aus dem Array wird die jeweilige Stelle verwendet welche den Tag darstellt
		JsonObject data0 = (JsonObject) data.getAsJsonArray().get(1);
		
		// System.out.println("heute: ("+data0.get("datetime")+")");
		tempWB0 		= data0.get("temp").getAsDouble(); 								// aktuelle Temp
		max_tempWB0 	= data0.get("max_temp").getAsDouble(); 							// maximal Temp
		low_tempWB0 	= data0.get("low_temp").getAsDouble(); 							// minimal Temp
		rhWB0 			= data0.get("rh").getAsDouble(); 								// Luftfeuchtigkeit
		precipitationWB = data0.get("pop").getAsDouble(); 								// Niederschlag
		wind_spdWB0 	= Math.round( data0.get("wind_spd").getAsDouble()*100)/100.;	// Wind
				
		//Image
		JsonObject Icon0 =  (JsonObject) data0.get("weather");
		iconWB0 = Icon0.get("icon").getAsString();
		//System.out.println("\tICON-ID:\t\t"+iconWB0);
		//ImageDownloader
		ImageDownloader five = new ImageDownloader();
		five.imageDownloaderWB(iconWB0);
		
	}
	public static String requestPlace() {
		String place = null;
		try {
			Scanner sc = new Scanner(System.in);
			System.out.print("Ort in Österreich eingeben: ");
			place = sc.next();
			//place.substring(0,1).toUpperCase()
			//https://attacomsian.com/blog/capitalize-first-letter-of-string-java#:~:text=The%20simplest%20way%20to%20capitalize,substring(0%2C%201).
			sc.close();
		} catch (NullPointerException e) {
			System.out.println("existierenden Ort eingeben");
			return null;
		} catch (Exception e) {
			System.out.println("[requestPlace]Fehler liegt nicht am Ort! Fehler: " + e);
		}
		return place;
	}
	public static void readCurrentWeatherAPI(String place) {
		//save current day weather API json request in String
		URL dUrl;
		try {
			dUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + place
					+ ",at&units=metric&appid=793d753c4a6623defbfafdce3d337e9b");
			HttpURLConnection dConn = (HttpURLConnection) dUrl.openConnection();
			dConn.setRequestMethod("GET");
			dConn.connect();
			
			int dResponsecode = dConn.getResponseCode();
			// System.out.println("Code: "+dResponsecode);
			if (dResponsecode != 200) { //200 ist code für Normalzustand (NWES Unterricht)
				System.out.println("Bitte sinnvollen Ort eingeben!");
				// System.out.println("[readCurrentWeatherAPI]HttpResonseCode: "+dResponsecode);
				// System.out.println("Help for responsecodes:
				// https://www.tutorialspoint.com/servlets/servlets-http-status-codes.htm");
			} else {
				Scanner sc = new Scanner(dUrl.openStream());
				while (sc.hasNext()) {
					dGetData += sc.nextLine();
				}
				// System.out.println(dInline);
				sc.close();
			}
		} catch (IOException e) {
			System.out
			.println("[readCurrentWeatherAPI] ungeklärter Fehler beim Einlesen der Current Weather API: " + e);
		}
	}
	public static void readForecastWeatherAPI(String place) {
		// save forecast weather API json request in String
		URL fcUrl;
		try {
			fcUrl = new URL("https://api.weatherbit.io/v2.0/forecast/daily?city="+place
					+"&country=at&days=9&lang=de&key=1c3d135c16f640c1823ce502f303e586"); //Bei days=6 oder 7 kommen andere Werte; 
			HttpURLConnection fcConn = (HttpURLConnection) fcUrl.openConnection();
			fcConn.setRequestMethod("GET");
			fcConn.connect();
			int fcResponsecode = fcConn.getResponseCode();
			// System.out.println("Code: "+fcResponsecode);
			if (fcResponsecode != 200) { //200 ist code für Normalzustand (NWES Unterricht)
				/*
				 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese
				 * Ausgabe erscheint System.out.println("Bitte sinnvollen Ort eingeben!");
				 * System.out.println("[readForecastWeatherAPI]HttpResonseCode: "+fcResponsecode
				 * ); System.out.
				 * println("Help for responsecodes: https://www.tutorialspoint.com/servlets/servlets-http-status-codes.htm"
				 * );
				 */
			} else {
				Scanner sc = new Scanner(fcUrl.openStream());
				while (sc.hasNext()) {
					fcGetData += sc.nextLine();
				}
				sc.close();
			}
		} catch (IOException e) {
			System.out.println(
					"[readForecastWeatherAPI] ungeklärter Fehler beim Einlesen der Forecast Weather API: " + e);
		}
	}
	public static double randomDoubleGenerator(double min, double max) {
		// min inklusive
		// max exklusive
		double r = Math.random() * (max - min) + min;
		double random = 0;
		if (min >= max) {
			System.out.println("Minimum muss kleiner als Maximum sein");
		} else
			random = Math.round(r * 100.0) / 100.0;
		return random;
	}
	public static void insertToDatabase(Connection con, String place) throws SQLException {
		String sql = "INSERT INTO temperatures values (NOW(),?,?,?,?,?)" + "	ON DUPLICATE KEY"
				+ "	UPDATE maxtempInCelsius = VALUES(maxtempInCelsius),"
				+ "	mintempInCelsius = VALUES(mintempInCelsius)," + "	humidityInPercent = VALUES(humidityInPercent),"
				+ "	windInKilometerPerHour = VALUES(windInKilometerPerHour);";
		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement(sql);
			stm.setString(1, place);
			stm.setDouble(2, max_tempAV);
			stm.setDouble(3, low_tempAV);
			stm.setDouble(4, humidityAV);
			stm.setDouble(5, windspeedAV);
			stm.executeUpdate();
		} catch (NullPointerException e) {
			/*
	 * Wird freigelassen weil bei falscher Ortseingabe bei jeder Methode diese
	 * Ausgabe erscheint
	 * System.out.println("[insertToDatabase] Sinnvollen Ort eingeben");
	*/
		} catch (Exception e) {
			System.out.println("[insertToDatabase] Fehler liegt nicht am Ort! Fehler:" + e);
		} finally {
			if (stm != null)
				stm.close();
		}
	}

}
