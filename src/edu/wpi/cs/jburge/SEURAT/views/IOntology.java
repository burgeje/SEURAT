package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Enumeration;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;


public abstract class IOntology extends ITreeViewerSetup {
	/**
	 * Created by Jon Wright, Team 2
	 */
	
	/**
	 * Add child ontology elements to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 */
	protected void addOntology( TreeParent parent, String parentName, int depth )
	{
		if ( depth == 0 ) {
			return;
		}
		
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getOntology(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			String childName = (String) onts.nextElement();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.ONTENTRY);
			parent.addChild(child);
			addOntology(child, childName, depth-1);
		}
		
	}

}
