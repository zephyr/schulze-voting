package dh.p.schulze;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Election {

	private String electionName;
	private Map<Integer, Person> persons;
	private Map<Integer, Candidate> exCandidates;
	private Map<Integer, Candidate> inCandidates;
	private Map<Person, List<List<Candidate>>> voteLists;
	private Map<Person, Matrix> voteMatrices;
	private Matrix duelMatrix;
	private Matrix pathMatrix;

	public Election(String electionName) {
		this.electionName = electionName;
		this.persons = Person.fromFile("votes/Personen.txt");
		this.exCandidates = Candidate.fromFile("votes/" + electionName
				+ ".Names.txt");

		this.inCandidates = new TreeMap<Integer, Candidate>();
		for (Candidate c : exCandidates.values()) {
			inCandidates.put(c.getId(), c);
		}

		readVotesFromFile("votes/" + electionName + ".Votes.txt");

		this.duelMatrix = newMatrix();
		for (Matrix m : voteMatrices.values()) {
			duelMatrix.add(m);
		}
		calculateStrongestPathMatrix();
	}

	protected int candidateNumber() {
		return exCandidates.keySet().size();
	}

	protected Matrix newMatrix() {
		return new Matrix(candidateNumber());
	}

	protected Set<Integer> allCandidates() {
		Set<Integer> set = new HashSet<Integer>();
		for (int k = 0; k < candidateNumber(); k++) {
			set.add(k);
		}
		return set;
	}

	protected void readVotesFromFile(String file) {
		this.voteLists = new TreeMap<Person, List<List<Candidate>>>();
		this.voteMatrices = new TreeMap<Person, Matrix>();

		List<String> lines = DataInput.readLines(file);
		for (String line : lines) {
			int split = line.indexOf(" ");
			Person person = persons.get(Integer.valueOf(line
					.substring(1, split)));
			String vote = line.substring(split + 1, line.length());

			List<List<Candidate>> list = new ArrayList<List<Candidate>>();
			Matrix m = newMatrix();
			Set<Integer> rest = allCandidates();

			String[] priorityString = vote.split(" ");
			for (String ps : priorityString) {
				String[] candidateString = ps.split("=");

				// create priority sets
				Set<Integer> priority = new HashSet<Integer>();
				List<Candidate> l = new ArrayList<Candidate>();
				for (String cs : candidateString) {
					int p = exCandidates.get(Integer.valueOf(cs)).getId();
					priority.add(p);
					l.add(inCandidates.get(p));
				}
				list.add(l);

				// apply the votes
				rest.removeAll(priority);
				for (int p : priority) {
					for (int k : rest) {
						m.inc(p, k);
					}
				}
			}
			if (rest.size() > 0) {
				List<Candidate> l = new ArrayList<Candidate>();
				for (int p : rest) {
					l.add(inCandidates.get(p));
				}
				list.add(l);
			}

			this.voteLists.put(person, list);
			this.voteMatrices.put(person, m);
		}
	}

	protected void calculateStrongestPathMatrix() {
		Matrix d = duelMatrix;
		Matrix p = newMatrix();
		int C = candidateNumber();

		for (int i = 0; i < C; i++) {
			for (int j = 0; j < C; j++) {
				if (i != j) {
					p.set(i, j, (d.get(i, j) > d.get(j, i)) ? d.get(i, j) : 0);
				}
			}
		}

		for (int i = 0; i < C; i++) {
			for (int j = 0; j < C; j++) {
				if (i != j) {

					for (int k = 0; i < C; i++) {
						if ((i != k) && (j != k)) {
							p.set(j, k, Math.max(p.get(j, k), Math.min(p.get(j,
									i), p.get(i, k))));
						}
					}
				}
			}
		}

		this.pathMatrix = p;
	}

	protected Candidate calculateCondorcetWinner() {
		Matrix d = duelMatrix;
		int C = candidateNumber();

		for (int i = 0; i < C; i++) {
			boolean b = true;
			for (int j = 0; j < C; j++) {
				if (i != j) {
					b = b && (d.get(i, j) > d.get(j, i));
				}
			}
			if (b) {
				return inCandidates.get(i);
			}
		}

		return null;
	}

	protected List<Candidate> calculatePotentialWinners() {
		List<Candidate> winnerList = new ArrayList<Candidate>();
		Matrix p = this.pathMatrix;
		int C = candidateNumber();

		for (int i = 0; i < C; i++) {
			boolean b = true;
			for (int j = 0; j < C; j++) {
				if (i != j) {
					b = b && (p.get(i, j) >= p.get(j, i));
				}
			}
			if (b) {
				winnerList.add(inCandidates.get(i));
			}
		}
		return winnerList;
	}

	public void createReceipts() {
		File out = new File("out");
		out.mkdir();

		for (Person p : voteLists.keySet()) {
			String filename = String.format("P%02d-%s-Wahl-%s.tex", p.getId(),
					p.getCasual(), electionName);
			Formatter f = DataOutput.getFormatter(out, filename);

			// f.format("\\documentclass{article}%n");
			f.format("\\documentclass[12pt, a4paper]{scrartcl}%n");
			f.format("\\usepackage{xltxtra}%n");
			f.format("\\setmainfont{Linux Libertine}%n");
			f.format("\\usepackage{polyglossia}%n");
			f
					.format("\\setdefaultlanguage[spelling=new, latesthyphen=true]{german}%n");
			f.format("\\begin{document}%n");
			f.format("Hallo %s,\\par%n", p.getCasual());
			f
					.format("dies ist ein automatisch generierte Beleg für Deine Stimmabgabe; bitte kontrolliere noch einmal, ob mir beim Übertragen auch kein Fehler unterlaufen ist! Deine Prioritäten sind:");

			f.format("\\begin{enumerate}%n");
			for (List<Candidate> priority : voteLists.get(p)) {
				f.format("\\item ");
				if (priority.size() == 1) {
					f.format("%s%n", priority.get(0).niceToString());
				} else {
					f.format("\\begin{itemize}%n");
					for (Candidate c : priority) {
						f.format("\\item %s%n", c.niceToString());
					}
					f.format("\\end{itemize}%n");
				}
			}
			f.format("\\end{enumerate}%n");
			f.format("\\end{document}%n");

			// f.format("Matrix:%n%s%n", voteMatrices.get(p));

			// f.flush();
			f.close();
		}
	}

	public void printResult() {
		Person.writeMasked("out/Personen-masked.txt", this.persons);
		Person.writeUnmasked("out/Personen-unmasked.txt", this.persons);

		Formatter f = DataOutput.getFormatter("out/Result-" + electionName
				+ ".txt");

		f.format("Vote: %s%n%n", electionName);

		f.format("Candidates:%n");
		for (Candidate c : inCandidates.values()) {
			f.format("%s%n", c);
		}
		f.format("%n");

		f.format("Persons who voted:%n");
		for (Person p : persons.values()) {
			f.format("%s%n", p);
		}
		f.format("%n");

		for (Person p : voteLists.keySet()) {
			f.format("%s voted:%n", p.getCasual());

			for (List<Candidate> priority : voteLists.get(p)) {
				f.format("%s%n", priority);
			}

			f.format("Resulting duel duelMatrix:%n%s%n",
          voteMatrices.get(p).toStringWith(inCandidates));
		}
		f.format("Final duel Matrix:%n%s", duelMatrix.toStringWith(inCandidates));
		f.format("Condorcet winner:  %s%n%n", calculateCondorcetWinner());
		f.format("Strongest path Matrix:%n%s", pathMatrix.toStringWith(inCandidates));
		f.format("Potential winners: %s%n%n", calculatePotentialWinners());

		f.close();

		f = DataOutput.getFormatter("out/Result-" + electionName + ".dot");

		f.format("digraph %s {%n", electionName);
		for (Candidate c : inCandidates.values()) {
			f.format("%d [label=\"%s\"];%n", c.getId(), c.niceToString());
		}

		int C = candidateNumber();
		for (int i = 0; i < C; i++) {
			for (int j = i + 1; j < C; j++) {
				int diff = duelMatrix.get(i, j) - duelMatrix.get(j, i);
				if (diff >= 0) {
					f.format("  %d -> %d%s;%n", i, j,
							(diff == 0) ? " [dir=both, style=\"dotted\"]" : "");
				} else {
					f.format("  %d -> %d;%n", j, i);
				}
			}
		}
		f.format("overlap=false;%n");
		f.format("splines=true;%n");
		f.format("}");
		f.close();

	}

	public static void main(String... arg) {
		for (String s : Arrays.asList("Navigationsblock", "Diakritika")) {
			Election e = new Election(s);
			e.createReceipts();
			e.printResult();
		}
	}

}
