package dh.p.schulze;

import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Person implements Comparable<Person> {

	private int id;
	private String eMail;
	private String firstName;
	private String surName;
	private String cognomen;

	private Person() {
	}

	public int getId() {
		return this.id;
	}

	public String getCasual() {
		return (cognomen != null) ? cognomen : firstName;
	}

	public String toString() {
		return String.format("(%d, %s %s, %s, %s)", id, firstName, surName,
				cognomen, eMail);
	}

	public int hashCode() {
		return this.id;
	}

	public int compareTo(Person p) {
		return this.getId() - p.getId();
	}

	public static Map<Integer, Person> fromFile(String file) {
		Map<Integer, Person> map = new TreeMap<Integer, Person>();
		List<String> lines = DataInput.readLines(file);

		for (String line : lines) {
			Person p = new Person();
			String[] i = line.split(" ");

			p.id = Integer.valueOf(i[0].substring(1));
			p.eMail = i[1].contains("@") ? i[1] : EMail.unmask(i[1]);

			if (i.length == 5) {
				p.firstName = i[2];
				p.surName = i[3];
				p.cognomen = i[4];
			} else if (i.length == 4) {
				p.firstName = i[2];
				p.surName = i[3];
			} else if (i.length == 3) {
				p.cognomen = i[2];
			}
			// System.out.format("%s -> %s%n", line, p); // debug
			map.put(p.id, p);
		}
		return map;
	}

	public static void writeMasked(String file, Map<Integer, Person> persons) {
		Formatter f = DataOutput.getFormatter(file);
		for (Person p : persons.values()) {
			f.format("P%d %s", p.id, EMail.mask(p.eMail));
			if (p.firstName != null) {
				f.format(" %s", p.firstName);
			}
			if (p.surName != null) {
				f.format(" %s", p.surName);
			}
			if (p.cognomen != null) {
				f.format(" %s", p.cognomen);
			}
			f.format("%n");
		}
		f.close();
	}

	public static void writeUnmasked(String file, Map<Integer, Person> persons) {
		Formatter f = DataOutput.getFormatter(file);
		for (Person p : persons.values()) {
			f.format("P%d %s", p.id, p.eMail);
			if (p.firstName != null) {
				f.format(" %s", p.firstName);
			}
			if (p.surName != null) {
				f.format(" %s", p.surName);
			}
			if (p.cognomen != null) {
				f.format(" %s", p.cognomen);
			}
			f.format("%n");
		}
		f.close();
	}
}
