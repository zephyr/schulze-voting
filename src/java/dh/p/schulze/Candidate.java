package dh.p.schulze;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

public class Candidate implements Comparable<Candidate> {

	private String name;
	private int internalId;
	private int externalId;

	private Candidate() {
	}

	public int getId() {
		return this.internalId;
	}

	public String toString() {
		return String.format("(%2d/%2d): %s", internalId, externalId, name);
	}

	public String niceToString() {
		return String.format("%s (Nr.: %d)", name, externalId);
	}
  
	public String niceAndShort() {
		return String.format("V%02d", externalId);
	}

	public int hashCode() {
		return this.internalId;
	}
  
	public int compareTo(Candidate p) {
		return this.getId() - p.getId();
	}

	public static Map<Integer, Candidate> fromFile(String file) {
		Map<Integer, Candidate> map = new TreeMap<Integer, Candidate>();
		List<String> lines = DataInput.readLines(file);
		int internalId = 0;

		for (String line : lines) {
			Candidate c = new Candidate();
			int split = line.indexOf(" ");

			int externalId = Integer.valueOf(line.substring(1, split));

			c.name = line.substring(split + 1, line.length());
			c.externalId = externalId;
			c.internalId = internalId;

			map.put(externalId, c);
			internalId++;
		}
		return map;
	}

}
