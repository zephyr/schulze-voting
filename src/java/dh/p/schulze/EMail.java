package dh.p.schulze;

public class EMail {

	public static String mask(String in) {
		StringBuilder out = new StringBuilder(in.length() * 7);

		for (int i = 0; i < in.length(); i++) {
			String binary = Integer.toString(in.codePointAt(i), 2).replace("0",
					"s").replace("1", "ſ");
			for (int j = 0; j < (7 - binary.length()); j++) {
				out.append("s");
			}
			out.append(binary);
		}

		return out.toString();
	}

	public static String unmask(String in) {
		StringBuilder out = new StringBuilder(in.length() / 7);

		for (int i = 0; i < in.length(); i += 7) {
			out.append((char) Integer.parseInt(in.substring(i, i + 7).replace(
					"s", "0").replace("ſ", "1"), 2));
		}

		return out.toString();
	}

}
