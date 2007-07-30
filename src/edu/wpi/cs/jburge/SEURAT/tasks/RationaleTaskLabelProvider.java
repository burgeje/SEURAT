/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 */

package edu.wpi.cs.jburge.SEURAT.tasks;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.wpi.cs.jburge.SEURAT.*;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/*
 * This is the label provider for our Rationale Task List
 * 
 * This code is based on the Eclipse Corner article 
 * "Building and delivering a table editor with SWT/JFace"
 * http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 * and on the example attached to the article:
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 *
 */
public class RationaleTaskLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {


	/**
	 * The name of the different error types
	 */
	public static final String ERROR_IMAGE 	= (RationaleErrorLevel.ERROR).toString();
	public static final String WARNING_IMAGE  = (RationaleErrorLevel.WARNING).toString();
	public static final String INFO_IMAGE  = (RationaleErrorLevel.INFORMATION).toString();
	

	/**
	 * Our image registry. An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */
	private static ImageRegistry imageRegistry = new ImageRegistry();

	//Add our icons to the registry
	static {
		imageRegistry.put(ERROR_IMAGE, SEURATPlugin.getImageDescriptor("error_tsk.gif")
			);
		imageRegistry.put(WARNING_IMAGE, SEURATPlugin.getImageDescriptor("warn_tsk.gif")
			);
		imageRegistry.put(INFO_IMAGE, SEURATPlugin.getImageDescriptor("info_tsk.gif")
			);

	}
	
	/**
	 * Returns the image with the given key, or <code>null</code> if not found.
	 * @param status - the rationale error level
	 */
	private Image getImage(RationaleErrorLevel status) {
		return  imageRegistry.get(status.toString());
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		RationaleTask task = (RationaleTask) element;
		switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN
				break;
			case 1 :
				result = task.getDescription();
				break;
			case 2 :
				result = task.getRationale();
				break;
			case 3 :
				result = task.getRationaleType() + "";
				break;
			default :
				break; 	
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return (columnIndex == 0) ?   // COMPLETED_COLUMN?
			getImage(((RationaleTask) element).getStatus()) :
			null;
	}

}
