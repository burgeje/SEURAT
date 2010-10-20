package edu.wpi.cs.jburge.SEURAT.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

public class AlternativePatternInferences {
	
	public Hashtable<String, Double> exactMatching(ArrayList<Pattern> toBeMatchedPatterns){
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> ourRequirements = db.getNFRs();
		
		Hashtable<String, Double> pattern_values = new Hashtable<String, Double>(); 
		//Vector<Pattern> allPatterns = db.getPatterns();
		//Enumeration ap = toBeMatchedPatterns.elements();
		System.out.println("====================================");
		System.out.println("Exact Matching Results");
		System.out.println("------------------------------------");
		for(Pattern pattern: toBeMatchedPatterns){
			Double valueOfPattern = 0.0;
			Enumeration rqus = ourRequirements.elements();
			while (rqus.hasMoreElements()){				
				boolean matched = false;
				Requirement q = (Requirement)rqus.nextElement();
				ArrayList<String> ontNames = new ArrayList<String>();
				for(OntEntry eo: (pattern.getPosiOnts())){
					if(eo.getName().compareTo(q.getOntology().getName()) == 0){
						matched = true;
					}
				}
				if (matched){
					String test = q.getImportance().toString();
					if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
						valueOfPattern = valueOfPattern + 1.0;
					}else{
						valueOfPattern = valueOfPattern + q.getImportance().getValue();
					}				
				}
			}
			pattern_values.put(pattern.getName(), new Double(valueOfPattern));
			System.out.println(pattern.getName());
			System.out.println(valueOfPattern);
		}
		System.out.println("");
		
		
		ArrayList myArrayList = new ArrayList(pattern_values.entrySet());
		
		Collections.sort(myArrayList, new MyComparator());
		System.out.println("====================================");
		System.out.println("Exact Matching Results after sorting");
		System.out.println("------------------------------------");
		
		Iterator itr = myArrayList.iterator();
		String key = "";
		Double value = 0.0;
		int cnt = 0;
		while(itr.hasNext()){ 

			cnt++;
			Map.Entry e=(Map.Entry)itr.next(); 

			key = (String)e.getKey();
			value = ((Double)e.getValue()).doubleValue();

			System.out.println(key + "," + value);

		}
		System.out.println("");
		
		//Vector<Pattern> candidatePattern = new Vector<Pattern>();
		Hashtable patterns_scores = new Hashtable();
		itr = myArrayList.iterator();
		while(itr.hasNext()){
			Map.Entry e=(Map.Entry)itr.next();
			String patternName = (String)e.getKey();
			Pattern newPattern = new Pattern();
			newPattern.fromDatabase(patternName);
			patterns_scores.put(newPattern, pattern_values.get(patternName));
			//candidatePattern.add(newPattern);
		}
		return patterns_scores;
	}
	
	
	public Hashtable<String, Double> contributionMatching(ArrayList<Pattern> toBeMatchedPatterns){
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> ourRequirements = db.getNFRs();
		
		Vector<Pattern> candidatePattern = new Vector<Pattern>();
		
		Hashtable<String, Double> pattern_values = new Hashtable<String, Double>(); 
		System.out.println("====================================");
		System.out.println("Contribution Matching Results");
		System.out.println("------------------------------------");
		for(Pattern pattern: toBeMatchedPatterns){
			Double valueOfPattern = 0.0;
			Enumeration rqus = ourRequirements.elements();
			while (rqus.hasMoreElements()){				
				boolean matched = false;
				Requirement q = (Requirement)rqus.nextElement();
				for(OntEntry eo: (pattern.getPosiOnts())){
					if(eo.getName().compareTo(q.getOntology().getName()) == 0){
						matched = true;
					}else{						
						RationaleDB d = RationaleDB.getHandle();
						Vector ontList = d.getOntologyElements(eo.getName());
						Enumeration ontChildren = ontList.elements();
						while (ontChildren.hasMoreElements()){
							OntEntry ont = (OntEntry)ontChildren.nextElement();
							if(ont.getName().compareTo(q.getOntology().getName()) == 0){
								matched = true;
							}
						}
					}
				}
				if(matched){
					if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
						valueOfPattern = valueOfPattern + 1.0;
					}else{
						valueOfPattern = valueOfPattern + q.getImportance().getValue();
					}	
				}
				
//				if (pattern.getPosiOnts().contains(q.assoOntEntry)){
//					//String test = q.getImportance().toString();
//					if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
//						valueOfPattern = valueOfPattern + 1.0;
//					}else{
//						valueOfPattern = valueOfPattern + q.getImportance().getValue();
//					}				
//				}else{
//					OntEntry o = new OntEntry();
//					o.fromDatabase(q.getOntEntry().getName());
//					RationaleDB d = RationaleDB.getHandle();
//					Vector ontList = d.getOntologyElements(o.getName());
//					Enumeration ontChildren = ontList.elements();
//					while (ontChildren.hasMoreElements()){
//						OntEntry ont = (OntEntry)ontChildren.nextElement();
//						//System.out.println(ont.getName());
//						if(pattern.getPosiOnts().contains(ont)){
//							//String test = q.getImportance().toString();
//							if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
//								valueOfPattern = valueOfPattern + 1.0;
//							}else{
//								valueOfPattern = valueOfPattern + q.getImportance().getValue();
//							}	
//						}
//					}					
//				}
			}
			pattern_values.put(pattern.getName(), new Double(valueOfPattern));
			System.out.println(pattern.getName());
			System.out.println(valueOfPattern);
		}
		System.out.println("");
		
		
		ArrayList myArrayList = new ArrayList(pattern_values.entrySet());
		
		Collections.sort(myArrayList, new MyComparator());

		System.out.println("====================================");
		System.out.println("Contribution Matching Results after soring");
		System.out.println("------------------------------------");
		Iterator itr = myArrayList.iterator();
		String key = "";
		Double value = 0.0;
		int cnt = 0;
		while(itr.hasNext()){ 

			cnt++;
			Map.Entry e=(Map.Entry)itr.next(); 

			key = (String)e.getKey();
			value = ((Double)e.getValue()).doubleValue();
			

			System.out.println(key + "," + value);

		}
		System.out.println("");
		
		Hashtable patterns_scores = new Hashtable();
		itr = myArrayList.iterator();
		while(itr.hasNext()){
			Map.Entry e=(Map.Entry)itr.next();
			String patternName = (String)e.getKey();
			Pattern newPattern = new Pattern();
			newPattern.fromDatabase(patternName);
			patterns_scores.put(newPattern, pattern_values.get(patternName));
			//candidatePattern.add(newPattern);
		}
		return patterns_scores;
	}
	
	static class MyComparator implements Comparator{

		public int compare(Object obj1, Object obj2){

			int result=0;Map.Entry e1 = (Map.Entry)obj1 ;

			Map.Entry e2 = (Map.Entry)obj2 ;//Sort based on values.

			Double value1 = (Double)e1.getValue();
			Double value2 = (Double)e2.getValue();

			if(value1.compareTo(value2)==0){

				String word1=(String)e1.getKey();
				String word2=(String)e2.getKey();

//				Sort String in an alphabetical order
				result=word1.compareToIgnoreCase(word2);

			} else{
//				Sort values in a descending order
				result=value2.compareTo( value1 );
			}

			return result;
		}
	}

	
	
}
