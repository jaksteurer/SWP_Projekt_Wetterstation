import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) throws MalformedURLException, IOException {

		readAPI();

	}

	public static void getImageOpenweather (Map<String, Object> m) {
		Image image = null;
		try {
			URL url = new URL("http://openweathermap.org/img/w/"+m+".png");
			image = ImageIO.read(url);
		} 
		catch (IOException e) {
		}
		// Use a label to display the image
		JFrame frame = new JFrame();
		JLabel lblimage = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(lblimage, BorderLayout.CENTER);
		frame.setSize(300, 400);
		frame.setVisible(true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(lblimage);
		// add more components here
		frame.add(mainPanel);
		frame.setVisible(true);
	}

	public static void getImageWeatherbit (Map<String, Object> m) {
		Image image = null;
		try {
			URL url = new URL("https://www.weatherbit.io/static/img/icons/"+m+".png");
			image = ImageIO.read(url);
		} 
		catch (IOException e) {
		}
		// Use a label to display the image
		JFrame frame = new JFrame();
		JLabel lblimage = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(lblimage, BorderLayout.CENTER);
		frame.setSize(300, 400);
		frame.setVisible(true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(lblimage);
		// add more components here
		frame.add(mainPanel);
		frame.setVisible(true);
	}

	public static void readAPI() {
		Gson gson = new Gson();
		String place = "Innsbruck";
		try {
			URL dayAPI = new URL("http://api.openweathermap.org/data/2.5/weather?q="+place+",at&units=metric&appid=793d753c4a6623defbfafdce3d337e9b");

			Object obj = null;
			gson.toJson(dayAPI);

			String json = gson.toJson(dayAPI);
			System.out.println(json);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	

}

