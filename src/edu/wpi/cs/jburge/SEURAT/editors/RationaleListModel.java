/*
 * Created on Mar 31, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import javax.swing.DefaultListModel;
import java.util.*;

/**
 * @author jburge
 *
 * Not used. 
 */
public class RationaleListModel {
	
	DefaultListModel ourModel;
	RationaleElementType type;
	RationaleElement parent;
	boolean dbList;
	
	RationaleListModel(RationaleElementType etype )
	{
		type = etype;
		dbList = true;
		RationaleDB db = RationaleDB.getHandle();
		ourModel = new DefaultListModel();
		Vector listV = db.getNameList(type);
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			ourModel.addElement( listE.nextElement());
		}
	}
	
	RationaleListModel(RationaleElementType etype, RationaleElement parnt)
	{
		dbList = false; //list comes from parent
		ourModel = new DefaultListModel();
		parent = parnt;
		type = etype;
		if (parent != null)
		{
			Vector listV = parent.getList(etype);
			Enumeration listE = listV.elements();
			while (listE.hasMoreElements())
			{
				ourModel.addElement( listE.nextElement());
			}
		}
	}
	
	Object elementAt(int index)
	{
		String itemName = (String) ourModel.elementAt(index);
		//need our item factory... so we can get it from the db...
		RationaleElement chosenElement = RationaleElementFactory.getRationaleElement(type);
		chosenElement.fromDatabase(itemName);
		return chosenElement;
	}
	
	int getSize()
	{
		return ourModel.getSize();
	}
	
	boolean notEmpty()
	{
		if (ourModel.getSize() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	void refresh()
	{
		ourModel.clear();
		
		if (dbList)
		{
			RationaleDB db = RationaleDB.getHandle();
			
			Vector listV = db.getNameList(type);
			Enumeration listE = listV.elements();
			while (listE.hasMoreElements())
			{
				ourModel.addElement( listE.nextElement());
			}
		}
		else
		{
			if (parent != null)
			{
				Vector listV = parent.getList(type);
				Enumeration listE = listV.elements();
				while (listE.hasMoreElements())
				{
					ourModel.addElement( listE.nextElement());
				}
			}
		}
		
		
	}
	
	
	DefaultListModel getModel()
	{
		return ourModel;
	}
	
}
