package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.Enumeration;
import java.util.Vector;


/**
 * This class implements a way of scoring patterns, and compares the scores.
 * A pattern with more violations, less satisfactions, is generally less than that with less violations and more satisfactions.
 * @author yechen
 *
 */
public class PatternEvalScore implements Comparable<PatternEvalScore>{
	private Vector<Requirement> exactViol, contribViol, possibleViol,
	exactSati, contribSati, possibleSati;
	private Pattern pattern;
	
	public Pattern getPattern() {
		return pattern;
	}

	public Vector<Requirement> getExactViol() {
		return exactViol;
	}

	public Vector<Requirement> getContribViol() {
		return contribViol;
	}

	public Vector<Requirement> getPossibleViol() {
		return possibleViol;
	}

	public Vector<Requirement> getExactSati() {
		return exactSati;
	}

	public Vector<Requirement> getContribSati() {
		return contribSati;
	}

	public Vector<Requirement> getPossibleSati() {
		return possibleSati;
	}

	public PatternEvalScore(Pattern pattern){
		this.pattern = pattern;
		exactViol = new Vector<Requirement>();
		contribViol = new Vector<Requirement>();
		possibleViol = new Vector<Requirement>();
		exactSati = new Vector<Requirement>();
		contribSati = new Vector<Requirement>();
		possibleSati = new Vector<Requirement>();
	}
	
	public void contributionMatching(){
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> ourRequirements = db.getNFRs();
		for (Requirement q: ourRequirements){
			boolean matched = false, matchedNeg = false;
			//Satisfactions
			for(OntEntry eo: (pattern.getPosiOnts())){
				if(eo.getName().compareTo(q.getOntology().getName()) == 0){
					exactSati.add(q);
					break;
				}else{						
					RationaleDB d = RationaleDB.getHandle();
					Vector<OntEntry> ontList = d.getOntologyDescendents(q.getOntology().getName());
					Enumeration<OntEntry> ontChildren = ontList.elements();
					while (ontChildren.hasMoreElements()){
						OntEntry ont = (OntEntry)ontChildren.nextElement();
						if(ont.getName().compareTo(eo.getName()) == 0){
							contribSati.add(q);
							break;
						}
					}
					if (matched) break;
				}
				
				if (!matched){
					int level = db.findRelativeOntLevel(eo, q.getOntology());
					if (level > 0){
						possibleSati.add(q);
					}
				}
			}

			//Violations
			for(OntEntry eo: (pattern.getNegaOnts())){
				if(eo.getName().compareTo(q.getOntology().getName()) == 0){
					exactViol.add(q);
					break;
				}else{						
					RationaleDB d = RationaleDB.getHandle();
					Vector<OntEntry> ontList = d.getOntologyDescendents(q.getOntology().getName());
					Enumeration<OntEntry> ontChildren = ontList.elements();
					while (ontChildren.hasMoreElements()){
						OntEntry ont = (OntEntry)ontChildren.nextElement();
						if(ont.getName().compareTo(eo.getName()) == 0){
							contribViol.add(q);
							break;
						}
					}
					if (matchedNeg) break;
				}
				
				if (!matchedNeg){
					int level = db.findRelativeOntLevel(eo, q.getOntology());
					if (level > 0){
						possibleViol.add(q);
						
					}
				}
			}
		}
	}

	@Override
	public int compareTo(PatternEvalScore o) {
		if (pattern == o.getPattern()){
			return 0;
		}
		//One with more violations is "less than" one with less violations
		int comp = o.getExactViol().size() - exactViol.size();
		if (comp != 0) return comp; 
		comp = o.getContribViol().size() - contribViol.size();
		if (comp != 0) return comp;
		comp = o.getPossibleViol().size() - possibleViol.size();
		if (comp != 0) return comp;
		
		//One with less satisfactions is "less than" one with more satisfactions
		comp = exactSati.size() - o.getExactSati().size();
		if (comp != 0) return comp;
		comp = contribSati.size() - o.getContribSati().size();
		if (comp != 0) return comp;
		comp = possibleSati.size() - o.getPossibleSati().size();
		if (comp != 0) return comp;
		return 0;
	}
	
	public int getNumSatisfactions(){
		return exactSati.size() + contribSati.size() + possibleSati.size();
	}
	
	public int getNumViolations(){
		return exactViol.size() + contribViol.size() + possibleViol.size();
	}
	
	public String toString(){
		return pattern.getName() + 
				"(Satisfactions: " + getNumSatisfactions() +
				", Violations: " + getNumViolations() + ")";
	}


}
