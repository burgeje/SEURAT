/*
 * Created on Jan 5, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author jburge
 *
 * Not used. The original idea was to use this to build special enumeration boxes but
 * they must not have been necessary.
 */
public class EnumerationBox extends JComboBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(ComboBoxModel arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(Object[] arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(Vector arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public EnumerationBox() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EnumerationBox(Enumeration typeEnum, String selected) {
		int i = 0;
		while (typeEnum.hasMoreElements())
		{
			String newItem = (String) typeEnum.nextElement();
			this.addItem(newItem);
			if (newItem.equals(selected))
			{
				this.setSelectedItem(this.getItemAt(i));
			}
			i++;
		}
	}
}
