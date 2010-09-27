package SEURAT.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;

/**
 * The base class for the input of the new SEURAT rationale editors.  Contains reference
 * methods to get the active rationale explorer, the target element, the parent element, and the
 * TreeParent representation of the element.  Also provides several other functions.
 * The various editors contain inner classes that extend this base class.
 */
public abstract class RationaleEditorInput implements IEditorInput {	
	/**
	 * The pattern library which opened this editor
	 */	
	private PatternLibrary patternLib;
	/**
	 * The rationale explorer which opened this editor
	 */
	private RationaleExplorer explorer;
	/**
	 * The rationale element which is being edited
	 */
	private RationaleElement targetElement;
	/**
	 * The parent of the rationale element being edited.
	 */
	private RationaleElement parentElement;
	/**
	 * The element in the rationale explorer tree which is the parent
	 * of this rationale element.
	 */
	private TreeParent parentTree;
	/**
	 * State variable for whether the elemtn is new. If true then
	 * the element will be added to the database, false if it
	 * already exists.
	 */
	private boolean creating = false;

	/**
	 * @param explorer RationaleExplorer
	 * @param tree the element in the RationaleExplorer tree
	 * @param parent the parent of the element associated with the logical file
	 * @param target the element to associate with the logical file
	 * @param new1 true if the element is being created, false if it already exists
	 */
	public RationaleEditorInput(
			RationaleExplorer pExplorer,
			TreeParent pTree,
			RationaleElement pParent, 
			RationaleElement pTarget, 
			boolean pNew) 
	{
		explorer = pExplorer;
		parentTree = pTree;
		parentElement = pParent;
		targetElement = pTarget;
		creating = pNew;
	}
	
	public RationaleEditorInput(
			PatternLibrary patternLib,
			TreeParent pTree,
			RationaleElement pParent, 
			RationaleElement pTarget, 
			boolean pNew) 
	{
		this.patternLib = patternLib;
		parentTree = pTree;
		parentElement = pParent;
		targetElement = pTarget;
		creating = pNew;
	}
	
	/**
	 * check whether the class object is of the same type
	 * as the type being wrapped by the logical file.
	 * 
	 * @param type the class to check
	 * @return true if type is the same type as the type the logical
	 * 		file is wrapping.
	 */
	public abstract boolean targetType(Class type);	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public abstract String getName();	
	
	/**
	 * Return Tooltip Text which is more specific than the getName function. By
	 * default the getName() function and getToolTipText() return the same
	 * information, however the tooltip can potentially provide a more detailed
	 * description of the data.
	 * 
	 */
	public String getToolTipText() {
		return getName();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if( targetType(adapter) ) {
			return targetElement;
		}
		if( adapter == RationaleExplorer.class ) {
			return explorer;
		}
		if( adapter == TreeParent.class ) {
			return parentTree;
		}
		if( adapter == RationaleElement.class ) {
			return parentElement;
		}
		if( adapter == PatternLibrary.class){
			return patternLib;
		}
		
		return null;
	}

	/**
	 * Used to determine whether the object exists in the filesystem.
	 * Currently not implemented.
	 */
	public boolean exists() {
		return false;
	}
	
	/**
	 * Equals method compares the two target elements.  This method is called by Eclipse
	 * when the user tries to open a new tab, and thus it effectively prevents having multiple
	 * editors open for the same rationale element.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof RationaleEditorInput) {
			RationaleElement otherEle = ((RationaleEditorInput)obj).targetElement;
			if (targetElement.getElementType() == otherEle.getElementType() &&
					targetElement.getName().equals(otherEle.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the Icon For This File.
	 * Method was auto-generated, but changing it doesn't seem to be necessary
	 * as the program gets the icons just fine as long as the icon name is
	 * specified in the plugin.xml entry for the editor.
	 */
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	/**
	 * Used to allow a persistent editor (so that it reloads after closing
	 * and opening Eclipse).  Currently not implemented.
	 */
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a boolean indicating whether or not this editor is creating a new element.
	 */
	public boolean isCreating() {
		return creating;
	}

	/**
	 * Sets the creating boolean to false.  This is called when we are saving a new
	 * element that we have created and is necessary to ensure the correctness of the editor.
	 */
	public void setCreated() {
		this.creating = false;
	}
	
	/**
	 * Sets the correct TreeParent object for an element.  This is called when we are saving a new
	 * element that we have created and is necessary to ensure consistency between the rationale
	 * database and the editor input.
	 */
	public void setTreeParent(TreeParent p) {
		parentTree = p;
	}

}