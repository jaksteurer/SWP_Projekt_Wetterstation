package application;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import Wetterstation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {

	//Wetterstation w = new Wetterstation();
	static String place;
	@FXML
	private Label ort, errormsg;
	@FXML
	private TextField textfeld;
	@FXML
	private Label currentT, maxT0, minT0, percip0, corrFact, humidity0, windspeed0;
	@FXML
	private Label date1, maxT1, minT1;
	@FXML
	private Label date2, maxT2, minT2;
	@FXML
	private Label date3, maxT3, minT3;
	@FXML
	private Label date4, maxT4, minT4;
	@FXML
	private Label date5, maxT5, minT5;
	@FXML
	private ImageView image0, image1, image2, image3, image4, image5;
	@FXML
	private Pane wData, search, err;

	public static void main(String[] args) {

		//requestPlace(null);

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub	
	}

	public void requestPlace(ActionEvent event) {
		//jeweiligen panes werden sichbar bzw unsichtbar gemacht
//		err.setVisible(false);
//		wData.setVisible(false);
//		search.setVisible(true);
		//Konfigurationsdatei
		ConfigFileReader confData = new ConfigFileReader();
		String defPlace = confData.getConfigFile();
		//Verbindung zur DB herstellen
		SQLConnection connector = new SQLConnection();
		Connection con;
		try {
			//eingegebener Text wird in variable gespeichert
			place = textfeld.getText();
			//Wenn d eingegeben wird, wird place auf den Default-Wert gesetzt
			if(place.equals("d")) place = defPlace;
			System.out.print("Ort eingegeben! -> ");
			System.out.println(place);
		}catch(Exception e) {
			System.out.println("[MainController.Orteingabe] Fehler: "+e);
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
		}
		//variable place wird an das label ort übergeben und im gui angezeigt
		try {
			Wetterstation.readCurrentWeatherAPI(place);
			Wetterstation.readForecastWeatherAPI(place);
			Wetterstation.showCurrentWeather(place);
			Wetterstation.showForecastWeather(place);
			//Werte werden zugewiesen
			ort.setText(place);
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
			date1.setText(Wetterstation.Datum(1));
			date2.setText(Wetterstation.Datum(2));
			date3.setText(Wetterstation.Datum(3));
			date4.setText(Wetterstation.Datum(4));
			date5.setText(Wetterstation.Datum(5));
			//Daten werden sichtbar gemacht
			search.setVisible(false);
			wData.setVisible(true);
		}catch (java.lang.ClassCastException e) {
			System.out.println("[MainController.java.lang.ClassCastException] Fehler: "+e);
			String msg = "Dieser Ort existiert in Österreich nicht!";
			errormsg.setText(String.valueOf(msg));
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
		}catch(Exception e) {
			System.out.println("[MainController.Methoden] Fehler: "+e);
			errormsg.setText(String.valueOf(e));
			err.setVisible(true);
			wData.setVisible(false);
			search.setVisible(false);
		}

		

		//		corrFact.setVisible(true);percip0.setVisible(true);maxT0.setVisible(true);
		//		minT0.setVisible(true);currentT.setVisible(true);maxT1.setVisible(true);
		//		minT1.setVisible(true);maxT2.setVisible(true);minT2.setVisible(true);
		//		maxT3.setVisible(true);minT3.setVisible(true);maxT4.setVisible(true);
		//		minT4.setVisible(true);maxT5.setVisible(true);minT5.setVisible(true);
		//		date1.setVisible(true);date2.setVisible(true);date3.setVisible(true);
		//		date4.setVisible(true);date5.setVisible(true);date3.setVisible(true);
		//Verbindung zur DB und Hochladen
		try {
			con = connector.getConnection();
			Wetterstation.insertToDatabase(con, place);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Fehler in der Datenbank: "+e);
		}
		//Bild aus dem Ordner anzeigen
		File file0 = new File("./pictures/"+Wetterstation.iconWB0+".png");
		Image image0 = new Image(file0.toURI().toString());
		this.image0.setImage(image0);
		File file1 = new File("./pictures/"+Wetterstation.iconWB1+".png");
		Image image1 = new Image(file1.toURI().toString());
		this.image1.setImage(image1);
		File file2 = new File("./pictures/"+Wetterstation.iconWB2+".png");
		Image image2 = new Image(file2.toURI().toString());
		this.image2.setImage(image2);
		File file3 = new File("./pictures/"+Wetterstation.iconWB3+".png");
		Image image3 = new Image(file3.toURI().toString());
		this.image3.setImage(image3);
		File file4 = new File("./pictures/"+Wetterstation.iconWB4+".png");
		Image image4 = new Image(file4.toURI().toString());
		this.image4.setImage(image4);
		File file5 = new File("./pictures/"+Wetterstation.iconWB5+".png");
		Image image5 = new Image(file5.toURI().toString());
		this.image5.setImage(image5);

		//SetNull();
	}
	public void SetNull() {
		Wetterstation.precipitationWB = 0;
		Wetterstation.max_tempAV = 0;
		Wetterstation.low_tempAV = 0;
		Wetterstation.tempAV = 0;
		Wetterstation.max_tempWB1 = 0;
		Wetterstation.low_tempWB1 = 0;
		Wetterstation.max_tempWB2 = 0;
		Wetterstation.low_tempWB2 = 0;
		Wetterstation.max_tempWB3 = 0;
		Wetterstation.low_tempWB3 = 0;
		Wetterstation.max_tempWB4 = 0;
		Wetterstation.low_tempWB4 = 0;
		Wetterstation.max_tempWB5 = 0;
		Wetterstation.low_tempWB5 = 0;
		Wetterstation.iconWB0 = null;
		Wetterstation.iconWB1 = null;
		Wetterstation.iconWB2 = null;
		Wetterstation.iconWB3 = null;
		Wetterstation.iconWB4 = null;
		Wetterstation.iconWB5 = null;
	}
}

