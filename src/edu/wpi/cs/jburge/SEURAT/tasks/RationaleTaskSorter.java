/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

package edu.wpi.cs.jburge.SEURAT.tasks;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * The sorter is what lets us click on a table header and sort the table by
 * those entries.
 * 
 * This code is based on the Eclipse Corner article 
 * "Building and delivering a table editor with SWT/JFace"
 * http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 * and on the example attached to the article:
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 
 */
public class RationaleTaskSorter extends ViewerSorter {
	
	/**
	 * Constructor argument values that indicate to sort items by 
	 * description, owner or percent complete.
	 */
	public final static int STATUS		 		= 1;
	public final static int DESCRIPTION			= 2;
	public final static int RATIONALE		 	= 3;
	public final static int RATIONALE_TYPE		= 4;
	
	// Criteria that the instance uses 
	private int criteria;
	
	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 *@param criteria - the sort criteria we are using
	 */
	public RationaleTaskSorter(int criteria) {
		super();
		this.criteria = criteria;
	}
	
	
	/**
	 * Used to figure out the order. Alphabetical for description, rationale
	 * element, and element type. For the status values,  it just groups like
	 * with like.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {
		
		RationaleTask task1 = (RationaleTask) o1;
		RationaleTask task2 = (RationaleTask) o2;
		
		switch (criteria) {
		case STATUS :
			return compareRationaleStatus(task1, task2);
		case DESCRIPTION :
			return compareDescriptions(task1, task2);
		case RATIONALE :
			return compareRationale(task1, task2);
		case RATIONALE_TYPE :
			return compareRationaleType(task1, task2);
		default:
			return 0;
		}
	}
	
	
	/**
	 * Compares two descriptions
	 * @param task1 - the first task
	 * @param task2 - the second task
	 * @return the comparison value (see collator)
	 */
	protected int compareDescriptions(RationaleTask task1, RationaleTask task2) {
		return collator.compare(task1.getDescription(), task2.getDescription());
	}
	
	
	/**
	 * Compares the names of the rationale elements
	 * @param task1 - the first task
	 * @param task2 - the second task
	 * @return the comparison value (see collator)
	 */
	protected int compareRationale(RationaleTask task1, RationaleTask task2) {
		return collator.compare(task1.getRationale(), task2.getRationale());
	}
	
	/**
	 * Compares the type
	 * @param task1 - the first task
	 * @param task2 - the second task
	 * @return the comparison results (see collator)
	 */
	protected int compareRationaleType(RationaleTask task1, RationaleTask task2) {
		return collator.compare(task1.getRationaleType(), task2.getRationaleType());
	}
	
	/**
	 * Compares the status values
	 * @param task1 - the first task
	 * @param task2 - the second task
	 * @return the results (see collator)
	 */
	protected int compareRationaleStatus(RationaleTask task1, RationaleTask task2) {
		return collator.compare(task1.getStatus().toString(), task2.getStatus().toString());
	}
	
	public int getCriteria() {
		return criteria;
	}
}
