package edu.wpi.cs.jburge.SEURAT.editors;

//import java.awt.Composite;

//import javax.swing.Scrollable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
//import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 * Display Utilities is a class that is used to set up some utility methods for
 * sizing display elements. The goal is to avoid the problems we were having where
 * display elements were fitted to the length of text strings, making them 
 * unusable.
 * @author burgeje
 *
 */
public class DisplayUtilities {
	
	/**
	 * Set up the dimensions (height and width for a list)
	 * @param ourWidget - the element being sized
	 * @param gridData - the gridData element being set up
	 * @param height - the height in lines of text
	 * @param width - the width - units??? 
	 */
	public static void setListDimensions(List ourWidget, GridData gridData, int height, int width )
	{
		int treeHeight = ourWidget.getItemHeight() * height;
		int treeWidth = ourWidget.getFont().getFontData().length * width;
		Rectangle trim = ourWidget.computeTrim(0, 0, treeWidth, treeHeight);
		gridData.widthHint = trim.width;
		gridData.heightHint = trim.height;
	}
	
	/**
	 * Set up the dimensions of a text widget
	 * @param ourWidget - the widget
	 * @param gridData - the data
	 * @param width - the desired width 
	 */
	public static void setTextDimensions(Text ourWidget, GridData gridData, int width)
	{
		int treeWidth = ourWidget.getFont().getFontData().length * width;
		Rectangle trim = ourWidget.computeTrim(0, 0, treeWidth, 0);
		gridData.widthHint = trim.width;	
	}
	
	/**
	 * Sets up the dimensions of a text widget spanning multiple lines of text
	 * @param ourWidget
	 * @param gridData
	 * @param width
	 * @param height
	 */
	public static void setTextDimensions(Text ourWidget, GridData gridData, int width, int height)
	{
		int treeWidth = ourWidget.getFont().getFontData().length * width;
		int treeHeight = ourWidget.getLineHeight() * height;
		Rectangle trim = ourWidget.computeTrim(0, 0, treeWidth, treeHeight);
		gridData.widthHint = trim.width;	
		gridData.heightHint = trim.height;
	}
	
	/**
	 * Sets up the width of a combo-box
	 * @param ourWidget
	 * @param gridData
	 * @param width
	 */
	public static void setComboDimensions(Combo ourWidget, GridData gridData, int width)
	{
		int treeWidth = ourWidget.getFont().getFontData().length * width;
		Rectangle trim = ourWidget.computeTrim(0, 0, treeWidth, 0);
		gridData.widthHint = trim.width;	
	}
	
	/**
	 * Sets up the height and width of a tree
	 * @param ourWidget
	 * @param gridData
	 * @param height
	 * @param width
	 */
	public static void setTreeDimensions(Tree ourWidget, GridData gridData, int height, int width )
	{
		int treeHeight = ourWidget.getItemHeight() * height;
		int treeWidth = ourWidget.getFont().getFontData().length * width;
		Rectangle trim = ourWidget.computeTrim(0, 0, treeWidth, treeHeight);
		gridData.widthHint = trim.width;
		gridData.heightHint = trim.height;
	}
	
}
