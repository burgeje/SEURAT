package SEURAT.search;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Default Resize Listener Implementation For SEURAT
 * Search Result Pages
 * 
 * @author hannasm
 */
public class SearchResultPage_TableResizeListener extends ControlAdapter 
{
	/**
	 * The table we are managing the size of
	 */
	private Table m_Table;
	
	/**
	 * Total Weight Of All Columns In Table
	 */
	private int m_TotalWeight;
	/**
	 * Used For Calculating Sizes Of Each Column In Table Relative To All Others
	 * 
	 * @author hannasm
	 */
	private class ColumnData
	{
		/**
		 * The Table Column To Track
		 */
		public TableColumn m_Column;
		/**
		 * Importance Of This Column When fitting columns to available space
		 */
		public int m_Weight;
		
		/**
		 * @param pColumn Column to resize
		 * @param pWeight importance of column when grabbing space
		 */
		public ColumnData(TableColumn pColumn, int pWeight)
		{
			m_Column = pColumn;
			m_Weight = pWeight;
		}
	}
	/**
	 * The list of columns we are resizing
	 */
	ArrayList<ColumnData> m_Columns;
	
	/**
	 * Tells the resize listener to resize this column.
	 * 
	 * @param pColumn The table column 
	 * @param pWeight The column size weight
	 */
	public void addColumn(TableColumn pColumn, int pWeight)
	{
		m_Columns.add(new ColumnData(pColumn, pWeight));
		m_TotalWeight += pWeight;
	}
	
	/**
	 * @param l_table The table we are managing the size of
	 */
	public SearchResultPage_TableResizeListener(Table l_table)
	{
		m_Table = l_table;
		m_Columns = new ArrayList<ColumnData>();
	}
	
	/* 
	 * Resize all tracked columns using weights every time a
	 * resize event occurs.
	 * 
	 * Adapted From: http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet77.java?view=co
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
		Rectangle area = m_Table.getParent().getClientArea();
		Point size = m_Table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = m_Table.getVerticalBar();
		int width = area.width - m_Table.computeTrim(0,0,0,0).width - vBar.getSize().x;
		if (size.y > area.height + m_Table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = m_Table.getSize();
		if (oldSize.x > area.width) {
			
			for( ColumnData l_column : m_Columns )
				l_column.m_Column.setWidth(width * l_column.m_Weight / m_TotalWeight);
			
			m_Table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table 
			// bigger first and then make the columns wider
			// to match the client area width
			m_Table.setSize(area.width, area.height);

			for( ColumnData l_column : m_Columns )
				l_column.m_Column.setWidth(width * l_column.m_Weight / m_TotalWeight);
		}
	}
}
