package application;

import java.net.URL;
import java.util.ResourceBundle;
import Wetterstation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController implements Initializable {
	
	Wetterstation w = new Wetterstation();
	static String place;
	@FXML
	private Label ort;
	@FXML
	private Button button;
	@FXML
    private TextField textfeld;
	@FXML
	private Label currentT, maxT0, minT0, percip0;
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
	
	public static void main(String[] args) {
		
		//requestPlace(null);
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub		
	}

	public void requestPlace(ActionEvent event) {
		//eingegebener Text wird in variable gespeichert
		place = textfeld.getText();
		System.out.println("Ort eingegeben!");
		//variable place wird an das label ort übergeben und im gui angezeigt
		ort.setText(place);
		Wetterstation.readCurrentWeatherAPI(place);
		Wetterstation.readForecastWeatherAPI(place);
		Wetterstation.showCurrentWeather(place);
		Wetterstation.showForecastWeather(place);
		//Werte werden zugewiesen
		percip0.setText(String.valueOf(Wetterstation.precipitationWB)+" %"); 
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
		//System.out.println(Wetterstation.iconWB0);
		
		//Bild laden (funktioniert noch nicht)
		image0.setImage(new Image("./pictures/c04d.png"));
		place=null;
	}

}

