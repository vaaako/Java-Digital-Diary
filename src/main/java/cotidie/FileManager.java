import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class FileManager {
	static final String FILE_PATH = System.getProperty("user.home")+"/.cotidie";
	static final String FILE_NAME = "cotidie-data.json";
	static final String FILE_FULL_PATH = FILE_PATH + '/' + FILE_NAME;

	static JSONObject mainObject;

	FileManager() {
		mainObject = getFile(FILE_PATH); // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
	}

	public static JSONObject getFile(String path) {
		try {
			File directory = new File(path);
			if(!directory.exists()) {
				directory.mkdir(); // Make directory if don't exist
			}

			File jsonFile = new File(FILE_FULL_PATH);
			if(!jsonFile.exists()) {
				// System.out.println("No file, creating one!");
				jsonFile.createNewFile(); // Make file if don't exist
				return new JSONObject();
			}
			// System.out.println("File exists!");
			Object obj = new JSONParser().parse(new FileReader(jsonFile));
			return (JSONObject)obj;

		} catch(Exception e) {
			return new JSONObject(); // If some error occurs is probabily because the file is empty
		}
	}

	public static String getDayText(String year, String month, String day) {
		JSONObject jsonYear = (JSONObject)mainObject.get(year);
		if(jsonYear != null) {
			JSONObject jsonMonth = (JSONObject)jsonYear.get(month);
			if(jsonMonth != null) {
				System.out.println("Exist"); // Always gettin as exist, even if not, why?
				String jsonDay = (String)jsonMonth.get(day);
				return jsonDay;
			}
		}
		System.out.println("Get default");
		return "Day " + day + "\n\n"; // Default text
	}

	public static void writeToFile(String path, String filename, JSONObject object) throws IOException {
		FileWriter file = new FileWriter(FILE_FULL_PATH);
		BufferedWriter bw = new BufferedWriter(file);

		bw.write( object.toJSONString() );
		bw.flush(); bw.close();
	}

	// Just save in run path
	public static void backup(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

		bw.write(mainObject.toJSONString());
		bw.flush(); bw.close();
	}

	public static void writeToJSON(String year, String month, String day, String text) { // year, motnh, year, text
		try {
			JSONObject jsonYear = (JSONObject)mainObject.get(year);
			if(jsonYear == null) {
				mainObject.put(year, new JSONObject()); // Add year object to main object
				jsonYear = (JSONObject)mainObject.get(year);
				// If don't have year, don't have month
				jsonYear.put(month, new JSONObject()); // Add month object to year object
			}
			JSONObject jsonMonth = (JSONObject)jsonYear.get(month);

			jsonMonth.put(day, text);
			writeToFile(FILE_PATH, FILE_NAME, mainObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


	// Get file from resources
	// public static BufferedReader readFileOnResources(String path) {
	// 	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	// 	InputStream inputStream = classLoader.getResourceAsStream(path); // null if path dont't exist
		
	// 	if(inputStream == null) {
	// 		System.out.println("File don't exist");
	// 		return null;
	// 	}

	// 	InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	// 	BufferedReader reader = new BufferedReader(streamReader); // Return this to get any file
	// 	return reader;
	// }