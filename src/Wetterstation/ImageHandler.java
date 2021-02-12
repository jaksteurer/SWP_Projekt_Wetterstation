package Wetterstation;

public class ImageHandler {

	public static void main(String[] args) {
	}

	public String getImage(String iconId) {
		switch(iconId) {
		case"t01d": case"t01n": case"t02d": case"t02n": case"t03d": case"t03n":
			return "tstorms";
		case"t04d": case"t04n": case"t05d": case"t05n": 
			return "chancestorms";
		case"d01d": case"d01n": 
			return "chanceflurries";
		case"s01d": case"s01n":  
			return "chancesnow";
		case"s03d": case"s03n": case"s02d": case"s02n": 
			return "snow";
		case"s04d": case"s04n": case"s05d": case"s05n": 
			return "sleet";
		case"s06d": case"s06n": 
			return "flurries";
		case"d02d": case"d02n": case"d03d": case"d03n": 
			return "chanceflurries";
		case"r01d": case"r01n": case"r02d": case"r02n": 
			return "chancerain";
		case"r03d": case"r03n": case"r04d": case"r04n": case"r05d": case"r05n": case"r06d": case"r06n": case"f01d": case"f01n": 
			return "rain";
		case"a01d": case"a01n": case"a02d": case"a02n": case"a03d": case"a03n": case"a04d": case"a04n": case"a05d": case"a05n": case"a06d": case"a06n": 
			return "fog";
		case"c01d":
			return "clear";
		case"c01n":
			return "nt_clear";
		case"c02d":
			return "partlycloudy";
		case"c02n":
			return "nt_partlycloudy";
		case"c03d":
			return "partlysunny";
		case"c03n":
			return "nt_partlysunny";
		case"c04d":	case"c04n":			
			return "cloudy";
		case"u00d": case"u00n":
			return "unknown";
		}
		return "unknown";
	}
}
