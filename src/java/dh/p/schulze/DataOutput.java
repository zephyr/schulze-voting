package dh.p.schulze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Formatter;

public class DataOutput {

	public static Formatter getFormatter(File dir, String fileName) {
		return DataOutput.getFormatter(new File(dir, fileName));
	}

	public static Formatter getFormatter(String fileName) {
		return DataOutput.getFormatter(new File(fileName));
	}

	public static Formatter getFormatter(File out) {
		Formatter f = null;
		try {
			f = new Formatter(new OutputStreamWriter(new FileOutputStream(out),
					Charset.forName("UTF-8")));
		} catch (Exception e) {
			System.err.println(e);
		}
		return f;
	}

}
