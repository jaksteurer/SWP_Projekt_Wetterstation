import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.gson.Gson;

public class Wetterstation {

	static String dGetData = " ";
	static String fcGetData = " ";

	public static void main(String[] args) throws IOException {
		String ort = "Innsbruck";
		//save current day json request in String
		URL dUrl = new URL("http://api.openweathermap.org/data/2.5/weather?q="+ort+",at&units=metric&appid=793d753c4a6623defbfafdce3d337e9b");
		HttpURLConnection dConn = (HttpURLConnection)dUrl.openConnection();
		dConn.setRequestMethod("GET");
		dConn.connect();
		int dResponsecode = dConn.getResponseCode();
		System.out.println("Code: "+dResponsecode);
		if(dResponsecode != 200) {
			throw new RuntimeException("HttpResonseCode: "+dResponsecode);
		}else {
			Scanner sc = new Scanner(dUrl.openStream());
			while(sc.hasNext()) {
				dGetData +=sc.nextLine();
			}
			//System.out.println(dInline);
			sc.close();
		}	
		//save forecast weather json request in String
		URL fcUrl = new URL("https://api.weatherbit.io/v2.0/forecast/daily?city="+ort+"&country=at&days=6&lang=de&key=1c3d135c16f640c1823ce502f303e586");
		HttpURLConnection fcConn = (HttpURLConnection)fcUrl.openConnection();
		fcConn.setRequestMethod("GET");
		fcConn.connect();
		int fcResponsecode = fcConn.getResponseCode();
		//System.out.println("Code: "+fcResponsecode);
		if(fcResponsecode != 200) {
			throw new RuntimeException("HttpResonseCode: "+fcResponsecode);
		}else {
			Scanner sc = new Scanner(fcUrl.openStream());
			while(sc.hasNext()) {
				fcGetData +=sc.nextLine();
			}
			//System.out.println(fcInline);
			sc.close();
		}
		currentWeather();
		System.out.println();
		forecastWeather();
		//Datum(0);
	}
	public static void Datum(int datum) {
		// Instanz vom Typ Kalender wird erstellt
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
		case 0: System.out.println(dateFormat.format(now)); 		//today Datum(0);
		break;
		case 1: System.out.println(dateFormat.format(tomorrow)); 	//Tomorrow Datum(1);
		break;
		case 2: System.out.println(dateFormat.format(TwoDaysFc)); 	//2 days forecast Datum(2);
		break;	
		case 3: System.out.println(dateFormat.format(ThreeDaysFc));	//3 days forecast Datum(3);
		break;
		case 4: System.out.println(dateFormat.format(FourDaysFc));	//4 days forecast Datum(4);
		break;
		case 5: System.out.println(dateFormat.format(FiveDaysFc));	//5 days forecast Datum(5);
		break;
		default:System.out.println("Wert muss zwischen 0 und 5 liegen");
		}
	}
	public static void currentWeather() {
		System.out.println("------Current Wetter API------");
		//aktuelles Wetter
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			Map<String, Object> dailyWeatherMap = new Gson().fromJson(dGetData, Map.class);
			//Java Map ausgeben mit allen Werten und den Keys
			/*for (Map.Entry<String, Object> key : dailyWeatherMap.entrySet()) {
					System.out.println(key.getKey()+"="+key.getValue());
					}*/
			//Werte eines einzelnen Keys anzeigen lassen
			//System.out.println("Main: "+ dailyWeatherMap.get("main"));
			//Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			//und davon wieder Map.get("Objektname") herausfiltern um genau den
			//gewünschten Wert zu bekommen
			Map<String, Object> main = (Map<String, Object>) dailyWeatherMap.get("main");
			System.out.println("aktuelle Temperatur:\t\t"+main.get("temp")+" C°");
			System.out.println("max. Temperatur:\t\t"+main.get("temp_max")+" C°");
			System.out.println("min. Temperatur:\t\t"+main.get("temp_min")+" C°");
			System.out.println("Luftfeuchtigkeit:\t\t"+main.get("humidity")+" %");
			Map<String, Object> wind = (Map<String, Object>) dailyWeatherMap.get("wind");
			System.out.println("Wind:\t\t\t\t"+wind.get("speed")+" Km/h");
		}catch (Exception e){
			e.printStackTrace();	
		}
	}
	public static void forecastWeather() {
		//forecast Wetter
		System.out.println("------Forecast Wetter API------");
		try {
			//Konvertieren des Json Strings "inline" in eine Java Map
			Map<String, Object> forecastWeatherMap = new Gson().fromJson(fcGetData, Map.class);
			//Java Map ausgeben mit allen Werten und den Keys
//			for (Map.Entry<String, Object> key : forecastWeatherMap.entrySet()) {
//							System.out.println(key.getKey()+"="+key.getValue());
//							}
			//Werte eines einzelnen Keys anzeigen lassen
			//System.out.println("data: "+ forecastWeatherMap.get("data"));
			/*
			 * Werte des Keys (falls Key mehr Werte hat) in eine weiteren Map speichern
			 * und davon wieder Map.get("Objektname") herausfiltern um genau den
			 * gewünschten Wert zu bekommen
			 */
			List<Object> data = (List<Object>) forecastWeatherMap.get("data");
			//System.out.println("data: "+data);
			//forecast day 1
			Map<Object, Object>data1 = (Map<Object, Object>) data.get(0);
			System.out.println("heute: ("+data1.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data1.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data1.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data1.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data1.get("pop")+" %");
			//forecast day 2
			Map<Object, Object>data2 = (Map<Object, Object>) data.get(1);
			System.out.println("Morgen: ("+data2.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data2.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data2.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data2.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data2.get("pop")+" %");
			//forecast day 3
			Map<Object, Object>data3 = (Map<Object, Object>) data.get(2);
			System.out.println("in 2 Tagen: ("+data3.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data3.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data3.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data3.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data3.get("pop")+" %");
			//forecast day 4
			Map<Object, Object>data4 = (Map<Object, Object>) data.get(3);
			System.out.println("in 3 Tagen: ("+data4.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data4.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data4.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data4.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data4.get("pop")+" %");
			//forecast day 5
			Map<Object, Object>data5 = (Map<Object, Object>) data.get(4);
			System.out.println("in 4 Tagen:("+data5.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data5.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data5.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data5.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
			Map<Object, Object>data6 = (Map<Object, Object>) data.get(5);
			System.out.println("in 5 Tagen:("+data6.get("datetime")+")");
			System.out.println("\tMinimal Temperatur:\t"+data6.get("low_temp")+" C°");
			System.out.println("\tMaximal Temperatur:\t"+data6.get("max_temp")+" C°");
			System.out.println("\tLuftfeuchtigkeit:\t"+data6.get("rh")+" %");
			System.out.println("\tNiederschlag:\t\t"+data5.get("pop")+" %");
		}catch (Exception e){
			e.printStackTrace();	
		}
	}

	

}
