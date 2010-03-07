package dh.p.schulze;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DataInput {

	public static List<String> readLines(String url) {
		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader r = new BufferedReader(new InputStreamReader(
				DataInput.class.getClassLoader().getResourceAsStream(url),
				Charset.forName("UTF-8")));

		try {
			String line;
			while ((line = r.readLine()) != null) {
				if ((line.length() > 0) && (line.startsWith("#") == false)) {
					lines.add(line);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		return lines;
	}

}
