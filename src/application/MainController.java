package application;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import Wetterstation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {

	//Wetterstation w = new Wetterstation();
	static String place;
	@FXML
	private Label ort, errormsg, wait;
	@FXML
	private TextField textfeld, newText;
	@FXML
	private Label currentT, percip0, corrFact, humidity0, windspeed0;
	@FXML
	private Label date1,date2, date3, date4, date5;
	@FXML
	private Label maxT0, maxT1, maxT2, maxT3, maxT4, maxT5;
	@FXML
	private Label minT0, minT1, minT2, minT3, minT4, minT5;
	@FXML
	private ImageView image0, image1, image2, image3, image4, image5, load;
	@FXML
	private Pane wData, search, err;
	//###################################
	//Details for each day (forecast)
	@FXML
	private Pane details, showdetails;
	@FXML
	private Button home, tomorrow, twodays, threedays, fourdays, fivedays;
	@FXML
	private Label dWindspeed, dPercip, dHumidity, dMaxT, dMinT, dPlace, ddt; //ddt = day,date,time
	@FXML
	private ImageView dImage;
	@FXML
	private ToggleButton tb;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Tag, Datum und Uhrzeit wird angezeigt
		//In dieser Klasse, da es dann sofort ausgeführt wird
		DateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat time = new SimpleDateFormat("HH:mm");
		String d= dt.format(new Date())+"   "+time.format(new Date());
		ddt.setText(Wetterstation.getWeekday(Wetterstation.Datum(0))+", "+d); 
	}

	public void tbPressed() {	
		//ToggleButtonHandler
		if(tb.isSelected()) {
			tb.setText("AT");
			Wetterstation.land = "at";
		}else if(tb.isSelected()==false) {
			tb.setText("DE");
			Wetterstation.land = "de";	
		}
	}

	public void start(/*ActionEvent event*/) {
		//#########################################################################################
		//Konfigurationsdatei aufrufen wenn d als Ort eingegeben wird
		ConfigFileReader confData = new ConfigFileReader();
		String defPlace = confData.getConfigFile();
		//#########################################################################################
		//Instanz für DB erstellen
		SQLConnection connector = new SQLConnection();
		Connection con;
		//#########################################################################################
		try {
			//eingegebener Text wird in variable gespeichert
			//.trim() löscht leerzeichen z.B. "     wien" --> "Wien"
			place = Wetterstation.capitalizeFirstLetter(textfeld.getText().trim());
			textfeld.setText(place);
			//Wenn d eingegeben wird, wird place auf den Default-Wert gesetzt
			if(place.equals("d")||place.equals("D")) place = defPlace;
			System.out.println("Ort eingegeben! -> "+ place);
		}catch(Exception e) {
			System.out.println("[MainController.Orteingabe,start] Fehler: "+e);
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
			details.setVisible(false);
			showdetails.setVisible(false);
		}
		//#########################################################################################
		try {
			//Verbindung zur DB herstellen
			con = connector.getConnection();
			//Methoden für die Werte aufrufen
			Wetterstation.readCurrentWeatherAPI(place);
			Wetterstation.readForecastWeatherAPI(place);
			Wetterstation.showCurrentWeather(place);
			Wetterstation.showForecastWeather(place);
			//Werte werden zugewiesen
			ort.setText(String.valueOf(Wetterstation.zipcodePlace));
			corrFact.setText(String.valueOf(Wetterstation.corrFact)+" °C");
			percip0.setText(String.valueOf(Wetterstation.precipitationWB)+" %");
			humidity0.setText(String.valueOf(Wetterstation.humidityAV)+" %");
			windspeed0.setText(String.valueOf(Wetterstation.windspeedAV)+" km/h");
			maxT0.setText(String.valueOf(Wetterstation.max_tempAV)+" °C");
			minT0.setText(String.valueOf(Wetterstation.low_tempAV)+" °C");
			currentT.setText(String.valueOf(Wetterstation.tempAV)+" °C");
			maxT1.setText(String.valueOf(Wetterstation.max_tempWB1)+" °C");
			minT1.setText(String.valueOf(Wetterstation.low_tempWB1)+" °C");
			maxT2.setText(String.valueOf(Wetterstation.max_tempWB2)+" °C");
			minT2.setText(String.valueOf(Wetterstation.low_tempWB2)+" °C");
			maxT3.setText(String.valueOf(Wetterstation.max_tempWB3)+" °C");
			minT3.setText(String.valueOf(Wetterstation.low_tempWB3)+" °C");
			maxT4.setText(String.valueOf(Wetterstation.max_tempWB4)+" °C");
			minT4.setText(String.valueOf(Wetterstation.low_tempWB4)+" °C");
			maxT5.setText(String.valueOf(Wetterstation.max_tempWB5)+" °C");
			minT5.setText(String.valueOf(Wetterstation.low_tempWB5)+" °C");
			date1.setText(Wetterstation.getWeekday(Wetterstation.Datum(1)));
			date2.setText(Wetterstation.getWeekday(Wetterstation.Datum(2)));
			date3.setText(Wetterstation.getWeekday(Wetterstation.Datum(3)));
			date4.setText(Wetterstation.getWeekday(Wetterstation.Datum(4)));
			date5.setText(Wetterstation.getWeekday(Wetterstation.Datum(5)));
			//Aus Datum wird der Tag berechnet und angezeigt
			tomorrow.setText(Wetterstation.getWeekday(Wetterstation.Datum(1)));
			twodays.setText(Wetterstation.getWeekday(Wetterstation.Datum(2)));
			threedays.setText(Wetterstation.getWeekday(Wetterstation.Datum(3)));
			fourdays.setText(Wetterstation.getWeekday(Wetterstation.Datum(4)));
			fivedays.setText(Wetterstation.getWeekday(Wetterstation.Datum(5)));
			//Tag, Datum und Uhrzeit wird angezeigt
			DateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat time = new SimpleDateFormat("HH:mm");
			String d= dt.format(new Date())+"   "+time.format(new Date());
			ddt.setText(Wetterstation.getWeekday(Wetterstation.Datum(0))+", "+d); 
			//#########################################################################################
			//Hochladen der Daten
			Wetterstation.insertToDatabase(con, place);
			//#########################################################################################
			//Daten werden sichtbar gemacht
			search.setVisible(false);
			wData.setVisible(true);
			details.setVisible(true);
			showdetails.setVisible(false);
			err.setVisible(false);
			//if(wData.isVisible()) load.setVisible(false);			
			//#########################################################################################
			Wetterstation.dGetData="";
			Wetterstation.fcGetData="";
		}catch (ClassNotFoundException | SQLException e) {
			//catch wenn Datenbanktproblem auftritt
			System.out.println("Fehler in der Datenbank: "+e);
			String msg ="Fehler in der Datenbank:\n"+e;
			errormsg.setText(String.valueOf(msg));
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
			details.setVisible(false);
			showdetails.setVisible(false);
		}catch (java.lang.ClassCastException e) {
			//catch wenn falsche Eingabe
			System.out.println("[MainController.java.lang.ClassCastException,start] Fehler: "+e);
			String msg ="";
			if(Wetterstation.land.equals("at")) {
				msg = "Dieser Ort existiert in Österreich nicht!";
			}else msg = "Dieser Ort existiert in Deutschland nicht!";
			errormsg.setText(String.valueOf(msg));
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
			details.setVisible(false);
			showdetails.setVisible(false);
		}catch(Exception e) {
			//alle anderen Error
			System.out.println("[MainController.Methoden,start] Fehler: "+e);
			errormsg.setText(String.valueOf(e));
			e.printStackTrace();
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
			details.setVisible(false);
			showdetails.setVisible(false);
		}
		//#########################################################################################
		//Bilder aus dem Ordner anzeigen
		File file0 = new File("./newIcons/"+Wetterstation.iconWB0+".png");
		Image image0 = new Image(file0.toURI().toString());
		this.image0.setImage(image0);
		File file1 = new File("./newIcons/"+Wetterstation.iconWB1+".png");
		Image image1 = new Image(file1.toURI().toString());
		this.image1.setImage(image1);
		File file2 = new File("./newIcons/"+Wetterstation.iconWB2+".png");
		Image image2 = new Image(file2.toURI().toString());
		this.image2.setImage(image2);
		File file3 = new File("./newIcons/"+Wetterstation.iconWB3+".png");
		Image image3 = new Image(file3.toURI().toString());
		this.image3.setImage(image3);
		File file4 = new File("./newIcons/"+Wetterstation.iconWB4+".png");
		Image image4 = new Image(file4.toURI().toString());
		this.image4.setImage(image4);
		File file5 = new File("./newIcons/"+Wetterstation.iconWB5+".png");
		Image image5 = new Image(file5.toURI().toString());
		this.image5.setImage(image5);
		//#########################################################################################
	}
	public void home() {
		search.setVisible(false);
		wData.setVisible(true);
		details.setVisible(true);
		showdetails.setVisible(false);
	}
	public void showDetailsTomorrow() {
		dPlace.setText(String.valueOf(Wetterstation.zipcodePlace));
		dMaxT.setText(String.valueOf(Wetterstation.max_tempWB1)+" °C");
		dMinT.setText(String.valueOf(Wetterstation.low_tempWB1)+" °C");
		dHumidity.setText(String.valueOf(Wetterstation.dHumidityWB1)+" %");
		dPercip.setText(String.valueOf(Wetterstation.dPercipWB1)+" %");
		dWindspeed.setText(String.valueOf(Wetterstation.dWindspeedWB1)+" Km/h");
		//Image
		File file = new File("./newIcons/"+Wetterstation.iconWB1+".png");
		Image image = new Image(file.toURI().toString());
		this.dImage.setImage(image);

		search.setVisible(false);
		wData.setVisible(false);
		showdetails.setVisible(true);
		details.setVisible(true);
	}
	public void showDetailsTwoDays() {
		dPlace.setText(String.valueOf(Wetterstation.zipcodePlace));
		dMaxT.setText(String.valueOf(Wetterstation.max_tempWB2)+" °C");
		dMinT.setText(String.valueOf(Wetterstation.low_tempWB2)+" °C");
		dHumidity.setText(String.valueOf(Wetterstation.dHumidityWB2)+" %");
		dPercip.setText(String.valueOf(Wetterstation.dPercipWB2)+" %");
		dWindspeed.setText(String.valueOf(Wetterstation.dWindspeedWB2)+" Km/h");
		//Image
		File file = new File("./newIcons/"+Wetterstation.iconWB2+".png");
		Image image = new Image(file.toURI().toString());
		this.dImage.setImage(image);
		//Panes sichtbar machen
		search.setVisible(false);
		wData.setVisible(false);
		showdetails.setVisible(true);
		details.setVisible(true);

	}
	public void showDetailsThreeDays() {
		dPlace.setText(String.valueOf(Wetterstation.zipcodePlace));
		dMaxT.setText(String.valueOf(Wetterstation.max_tempWB3)+" °C");
		dMinT.setText(String.valueOf(Wetterstation.low_tempWB3)+" °C");
		dHumidity.setText(String.valueOf(Wetterstation.dHumidityWB3)+" %");
		dPercip.setText(String.valueOf(Wetterstation.dPercipWB3)+" %");
		dWindspeed.setText(String.valueOf(Wetterstation.dWindspeedWB3)+" Km/h");
		//Image
		File file = new File("./newIcons/"+Wetterstation.iconWB3+".png");
		Image image = new Image(file.toURI().toString());
		this.dImage.setImage(image);
		//Panes sichtbar machen
		search.setVisible(false);
		wData.setVisible(false);
		showdetails.setVisible(true);
		details.setVisible(true);
	}
	public void showDetailsFourDays() {
		dPlace.setText(String.valueOf(Wetterstation.zipcodePlace));
		dMaxT.setText(String.valueOf(Wetterstation.max_tempWB4)+" °C");
		dMinT.setText(String.valueOf(Wetterstation.low_tempWB4)+" °C");
		dHumidity.setText(String.valueOf(Wetterstation.dHumidityWB4)+" %");
		dPercip.setText(String.valueOf(Wetterstation.dPercipWB4)+" %");
		dWindspeed.setText(String.valueOf(Wetterstation.dWindspeedWB4)+" Km/h");
		//Image
		File file = new File("./newIcons/"+Wetterstation.iconWB4+".png");
		Image image = new Image(file.toURI().toString());
		this.dImage.setImage(image);
		//Panes sichtbar machen
		search.setVisible(false);
		wData.setVisible(false);
		showdetails.setVisible(true);
		details.setVisible(true);
	}
	public void showDetailsFiveDays() {
		dPlace.setText(String.valueOf(Wetterstation.zipcodePlace));
		dMaxT.setText(String.valueOf(Wetterstation.max_tempWB5)+" °C");
		dMinT.setText(String.valueOf(Wetterstation.low_tempWB5)+" °C");
		dHumidity.setText(String.valueOf(Wetterstation.dHumidityWB5)+" %");
		dPercip.setText(String.valueOf(Wetterstation.dPercipWB5)+" %");
		dWindspeed.setText(String.valueOf(Wetterstation.dWindspeedWB5)+" Km/h");
		//Image
		File file = new File("./newIcons/"+Wetterstation.iconWB5+".png");
		Image image = new Image(file.toURI().toString());
		this.dImage.setImage(image);
		//Panes sichtbar machen
		search.setVisible(false);
		wData.setVisible(false);
		showdetails.setVisible(true);
		details.setVisible(true);
	}




}

