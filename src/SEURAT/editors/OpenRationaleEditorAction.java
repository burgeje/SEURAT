package SEURAT.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.*;

import java.lang.reflect.*;

import java.util.*;

/**
 * Class For Automating The Process Of Opening A SEURAT Rationale Editor
 * Window Using The Eclipse Editor Framework Extension Point. Uses java reflection
 * to retrieve some information about the editor window class being opened
 * to simplify the interface significantly.
 */
public class OpenRationaleEditorAction extends Action {
	/**
	 * Pattern library object to be edited, analogous to RationaleExplorer.
	 */
	PatternLibrary patternLib;
	
	/**
	 * The class object that is going to be used to 
	 * instantiate a new editor window.
	 */
	Class editorClass;
	
	/**
	 * The RationaleExplorer which is responsible for presenting the rationale
	 * element or elements being edited.
	 */
	RationaleExplorer explorer;
	
	/**
	 * State Variable That Indicates Whether The Rationale Editor Being Opened
	 * Is Creating A New Rationle Element Or Editing An Existing
	 */
	boolean isNew;
	
	/**
	 * A Helper Variable Which Indicates The Exact Type Of The Rationale Element Editor
	 * Being Opened using SEURAT's internal object factory.
	 */
	RationaleElementType reqType;
	
	TreeParent targetTreeParent;
	
	/**
	 * Construct A Rationale Editor Action
	 * 
	 * @param pClass The Class Of The Rationale Editor Instantiated
	 * @param pOwner The RationaleExplorer which needs updated when the editor saves
	 * @param pNew State variable indicating whether the editor will be creating a new or
	 * 			editing an existing Ratioanle Element
	 */
	public OpenRationaleEditorAction(Class pClass, RationaleExplorer pOwner, boolean pNew)
	{
		editorClass = pClass;
		explorer = pOwner;
		isNew = pNew;
		reqType = null;
		targetTreeParent = null;
	}
	
	public OpenRationaleEditorAction(Class pClass, PatternLibrary pOwner, boolean pNew)
	{
		editorClass = pClass;
		patternLib = pOwner;
		isNew = pNew;
		reqType = null;
	}
	
	/**
	 * Construct A Rationale Editor Action
	 * 
	 * @param pClass The Class Of The Rationale Editor Instantiated
	 * @param pOwner The RationaleExplorer which needs updated when the editor saves
	 * @param pNew State variable indicating whether the editor will be creating a new or
	 * 			editing an existing Ratioanle Element
	 * @param rType the type of object being edited
	 */
	public OpenRationaleEditorAction(Class pClass, RationaleExplorer pOwner, boolean pNew, RationaleElementType rType)
	{
		editorClass = pClass;
		explorer = pOwner;
		isNew = pNew;		
		reqType = rType;  
		targetTreeParent = null;
	}
	
	public OpenRationaleEditorAction(Class pClass, RationaleExplorer pOwner, TreeParent pParent)
	{		
		editorClass = pClass;
		explorer = pOwner;
		isNew = false;
		reqType = null;
		targetTreeParent = pParent;
	}
	
	public OpenRationaleEditorAction(Class pClass, PatternLibrary pOwner, boolean pNew, RationaleElementType rType)
	{
		editorClass = pClass;
		patternLib = pOwner;
		isNew = pNew;		
		reqType = rType;  
	}
	
	/**
	 * Standard action listener event function. Retrieve or create the rationale element
	 * needed for the editor to open and then instructs eclipse to load the edtior. For this function
	 * to succeed a number of conditions must be satisified.
	 * 
	 * 1) The editor class must be a valid implementation of the Eclipse Editor Window interface
	 * 2) The class object being implemented must have a public static 
	 * 		function called createInput with the signature
	 * 		createInput(RationaleExplorer,TreeParent,RationaleElement,RationaleElement,boolean)
	 * 3) The Eclipse Editor Must Expect or be capable of working with an eclipse input site
	 * 		of the type RationaleEditorInput.
	 * 4) The class must be defined in the SEURAT plugin.xml file as an editor extension point
	 * 		with an id of the full class name, including packages
	 * 
	 * The above conditions are special cases and points 2, 3, and 4 will not generate
	 * compilation errors if they are not satisfied. Attemtping to open an editor with either
	 * point 2 or 3 violated will result in a runtime exception being generated in this function
	 * which will result in a full stack trace being printed to the error console. Violation of
	 * point 4 will cause undefined behavior which through experience results in eclipse simply
	 * not opening the editor window.
	 */
	public void run() {

		try {			
			// Get Selected Node In The Rationale Explorer
			TreeParent tree;
		/*	if( pTarget != null )
			{
				explorer.
			}*/
			
			if( targetTreeParent != null ){
				tree = targetTreeParent;
			}
			//editors that may be invoked without selecting an item will need special attention
			else if(editorClass.getName().equals("SEURAT.views.GraphicalRationale")){
				tree = new TreeParent("imaginary root", RationaleElementType.RATIONALE);
			}else{
				tree = (TreeParent)((IStructuredSelection)explorer.getViewer().getSelection()).getFirstElement();
			}	
			RationaleElement rElement;
			if (reqType == null) rElement = explorer.getElement(tree, isNew);
			else rElement = explorer.getElement(new TreeObject("unused", reqType), true);
			RationaleElement rParent = explorer.getElement(tree, false);

			Class parameterTypes[] = {
					RationaleExplorer.class,
					TreeParent.class,
					RationaleElement.class,
					RationaleElement.class,
					Boolean.TYPE
			};
			Object parameters[] = { explorer, tree, rParent, rElement, isNew };
			
			Method getInput = editorClass.getMethod("createInput", parameterTypes);
			RationaleEditorInput data = (RationaleEditorInput)getInput.invoke(null, parameters);

			// Get The Workbench Information
			IWorkbenchPage l_page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			String l_className = editorClass.getName();
			l_page.openEditor(data, l_className);
		} catch (Exception eError) {
			eError.printStackTrace();
		}
		
	}
}
