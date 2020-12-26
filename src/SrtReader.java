import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JLabel;

import javafx.scene.media.MediaPlayer;

public class SrtReader {
	private BufferedReader reader;
	private ParseLyric parser;

	public SrtReader(String src) {
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			parser = new ParseLyric();
			String str = reader.readLine();
			str = reader.readLine();
			String[] strArr = str.split(" ");
			String[] from = strArr[0].split(":");
			String[] to = strArr[2].split(":");
			// clear the time interval before
			str = reader.readLine();
			String result = "";
			// skip enter
			while (!str.equals("")) {
				result += " " + str;
				str = reader.readLine();
			}
			parser.add(calTime(from), calTime(to), result);

			while (reader.ready()) {
				str = reader.readLine();

				try {
					// try to read the first integer
					int i = Integer.parseInt(str);
					str = reader.readLine();
					strArr = str.split(" ");
					from = strArr[0].split(":");
					to = strArr[2].split(":");
					str = reader.readLine();
					result = "";
					// skip enter
					while (!str.equals("")) {
						result += " " + str;
						str = reader.readLine();
					}
					parser.add(calTime(from), calTime(to), result);
				} catch (NumberFormatException e) {
				}

			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void set(JLabel lyric, MediaPlayer mp) {
		String l = parser.search(mp.getCurrentTime().toSeconds());
		if (l != null) {
			lyric.setText(l);
		}

	}

	public double calTime(String[] time) {
		double result = 0;
		String[] sec = time[2].split(",");
		result = Double.parseDouble(time[0]) * 60;
		result = (result + Double.parseDouble(time[1])) * 60;
		result = result + Double.parseDouble(sec[0]) + Double.parseDouble(sec[1]) / 1000;
		return result;
	}
}
