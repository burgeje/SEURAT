/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package SEURAT.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.RationaleTreeMap;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import SEURAT.editors.OpenRationaleEditorAction;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Utility Functions Which Really Aren't Even  
 * Related to Search Functionality. However most of these
 * methods haven't been implemented in some standard
 * location yet.
 * 
 * TODO: Move this class somewhere more appropriate
 * 
 * @author hannasm
 */
public class Utilities {
	/**
	 * Find the RationaleExplorer in the workspace. This 
	 * will perform a search throughout all open workbenches
	 * and is probably a horrible way of finding the rationale
	 * explorer.
	 * 
	 * @param pElement any rationale element which should be in
	 * 		the rationale explorer.
	 * 
	 * @return a reference to the best RationaleExplorer or null if
	 * 			none was found.
	 */
	public static RationaleExplorer getExplorer(RationaleElement pElement)
	{		
		TreeParent l_parent = null;
		RationaleExplorer l_explorer = null;
		IWorkbench wb = PlatformUI.getWorkbench();
		for( IWorkbenchWindow win : wb.getWorkbenchWindows() )
		{
			for( IWorkbenchPage page : win.getPages() )
			{
				for( IViewReference view : page.getViewReferences() )
				{
					if( !(view.getView(false) instanceof RationaleExplorer) )
						continue;
					
					l_explorer = (RationaleExplorer)view.getView(false);
					
					l_parent = getTreeParentFromElement(l_explorer, pElement);
					
					if( l_parent != null )
						return l_explorer;					
				}
			}
		}
		return null;
	}
	
	/**
	 * @param pExplorer the rationale explorer that has the treeparent we want
	 * @param pElement the rationale element whose treeparent we want to find
	 * @return a pointer to the first treeparent that is associated with the rationale element
	 * 				or null if no tree parent could be found.
	 */
	public static TreeParent getTreeParentFromElement(RationaleExplorer pExplorer, RationaleElement pElement)
	{
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		Vector treeObjs = map.getKeys(map.makeKey(
				pElement.getName(), 
				pElement.getElementType()));
		
		if( treeObjs.size() <= 0 ) return null;
	
		return (TreeParent) treeObjs.elementAt(0);
	}
	
	/**
	 * This function will automatically deduce the appropriate
	 * editor to use when opening a rationale element.
	 * 
	 * @param pElement the rationale element to open in an editor
	 */
	public static void openEditorForRationale(RationaleElement pElement)
	{
		RationaleExplorer l_explorer = Utilities.getExplorer(pElement);
		
		if( l_explorer == null ) return;
	
		Class l_editorClass = Utilities.getEditorFromRationale(pElement);		
		if( l_editorClass == null ) {
			org.eclipse.swt.widgets.Display l_display;
			
			l_display = l_explorer.getViewer().getControl().getDisplay();
			
			boolean canceled = pElement.display(l_display);
			if (!canceled)
			{
				RationaleUpdateEvent evt = new RationaleUpdateEvent(pElement);
				evt.fireUpdateEvent(pElement, l_display, UpdateType.UPDATE);		
			}
			return;
		}
		
		TreeParent l_parent = Utilities.getTreeParentFromElement(l_explorer, pElement);
		
		OpenRationaleEditorAction l_openAction = new OpenRationaleEditorAction(
				l_editorClass, l_explorer, l_parent
		);
		l_openAction.run();
	}
	
	/**
	 * @param pElement the rationale element which wants an editor
	 * @return the integrated editor class which is most
	 * 			capable of editing the specified rationale
	 */
	public static Class getEditorFromRationale(RationaleElement pElement)
	{
		if( pElement.getElementType() == RationaleElementType.ALTERNATIVE )
			return SEURAT.editors.AlternativeEditor.class;
		if( pElement.getElementType() == RationaleElementType.ARGUMENT )
			return SEURAT.editors.ArgumentEditor.class;
		if( pElement.getElementType() == RationaleElementType.DECISION )
			return SEURAT.editors.DecisionEditor.class;
		if( pElement.getElementType() == RationaleElementType.QUESTION )
			return SEURAT.editors.QuestionEditor.class;
		if( pElement.getElementType() == RationaleElementType.REQUIREMENT )
			return SEURAT.editors.RequirementEditor.class;
		if( pElement.getElementType() == RationaleElementType.TRADEOFF || pElement.getElementType() == RationaleElementType.COOCCURRENCE)
			return SEURAT.editors.TradeoffEditor.class;
		if( pElement.getElementType() == RationaleElementType.CONTINGENCY )
			return SEURAT.editors.TradeoffEditor.class;
		
		return null;
	}

	/**
	 * Container mapping ImageDescriptors to actual icons,
	 * this is used to prevent duplicating heavy-duty image classes
	 * and reduced load times.
	 */
	private static Map<ImageDescriptor,Image> g_rationaleElementIcons = new HashMap<ImageDescriptor, Image>(11);
	
	public static Image getRationaleElementIcon(
		RationaleElementType pType, 
		RationaleErrorLevel pError,
		boolean pActive,
		String pName,
		RationaleElement pElement
	)
	{
		ImageDescriptor descriptor = null;
		if (pType == RationaleElementType.REQUIREMENT) {
			if( pError == null )
			{
				pError = RationaleDB.getHandle().getActiveStatus(pName, pType);
			}
			if (pError == RationaleErrorLevel.ERROR)
			{
				if (pActive)
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_error.gif");
				}
				else
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_error_Disabled2.gif");
					
				}
			}	 
			else if  (pError == RationaleErrorLevel.WARNING)
			{
				if (pActive)
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_warning.gif");		
				}
				else
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_warning_Disabled2.gif");		
				}
			}
			else
			{
				if (pActive)
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq.gif");
				}
				else
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_Disabled2.gif");				
				}
			}
		} else if (pType == RationaleElementType.DECISION) {
			if( pError == null )
			{
				pError = RationaleDB.getHandle().getActiveStatus(pName, pType);
			}
			if (pError == RationaleErrorLevel.ERROR)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec_error.gif");
			}	 
			else if  (pError == RationaleErrorLevel.WARNING)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec_warning.gif");		
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec.gif");
			}
		} else if (pType == RationaleElementType.ALTERNATIVE) {
			Alternative alt = new Alternative();
			alt.fromDatabase(pElement.getName());
			if (alt.getID() > 0 && alt.isUMLAssociated()){
				if (pError == RationaleErrorLevel.ERROR){
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_UML_error.gif");
				} else if (pError == RationaleErrorLevel.WARNING){
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_UML_warning.gif");
				} else {
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_UML.gif");
				}
			}
			if( pError == null )
			{
				pError = RationaleDB.getHandle().getActiveStatus(pName, pType);
			}
			if (pError == RationaleErrorLevel.ERROR)
			{
				if (pActive)
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel_error.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_error.gif");
			}	 
			else if  (pError == RationaleErrorLevel.WARNING)
			{
				if (pActive)
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel_warning.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_warning.gif");
			}
			else
			{
				if (pActive)
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt.gif");
				
			}
		} else if (pType == RationaleElementType.ARGUMENT) {
			//arguments require a bit more processing since we want to 
			//differentiate between arguments for and against
			Argument ourArg;
			if( pElement != null )
				ourArg = (Argument)pElement;
			else
				ourArg = (Argument)RationaleDB.getRationaleElement(pName, RationaleElementType.ARGUMENT);
			
			if (ourArg.isFor())
			{
				descriptor = SEURATPlugin.getImageDescriptor("argFor.gif");
			}
			else if (ourArg.isAgainst())
			{
				descriptor = SEURATPlugin.getImageDescriptor("argAgainst.gif");
			}
			//we will count arguments referring to other alternatives as neutral for
			//now - that is not entirely accurate but their status will change and it
			//will be complicated to keep track of
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newArg.gif");
			}
		} else if (pType == RationaleElementType.CLAIM) {
			descriptor = SEURATPlugin.getImageDescriptor("newClaim2.gif");
		} else if (pType == RationaleElementType.ALTCONSTREL)
		{
			descriptor = SEURATPlugin.getImageDescriptor("Constr_Rel.gif");
		} else if (pType == RationaleElementType.QUESTION) {
			if (pActive)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newQuest.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newQuest_Unanswered.gif");
			}
			
			
		} else if (pType == RationaleElementType.COOCCURRENCE) {
			descriptor = SEURATPlugin.getImageDescriptor("Cooc.gif");
		} else if (pType == RationaleElementType.ONTENTRY) {
			descriptor = SEURATPlugin.getImageDescriptor("newOnt.gif");
		} 
		else if (pType == RationaleElementType.DESIGNPRODUCTENTRY) {
			descriptor = SEURATPlugin.getImageDescriptor("product.gif");
		}
		else if (pType == RationaleElementType.EXPERTISE) {
			descriptor = SEURATPlugin.getImageDescriptor("product.gif");
		}
		else if (pType == RationaleElementType.CONSTRAINT)
		{
			descriptor = SEURATPlugin.getImageDescriptor("constraint.gif");
		}
		else if (pType == RationaleElementType.CONTINGENCY)
		{
			descriptor = SEURATPlugin.getImageDescriptor("claim.gif");
		}
		else if (pType == RationaleElementType.DESIGNER)
		{
			descriptor = SEURATPlugin.getImageDescriptor("designer.gif");
		}
		else if (pType == RationaleElementType.ASSUMPTION) 
		{
			if (pActive)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAssump.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAssump_Disabled.gif");
			}
			
		} else if (pType == RationaleElementType.RATIONALE) {
			descriptor = SEURATPlugin.getImageDescriptor("Rat2.gif");
		} else if (pType == RationaleElementType.TRADEOFF) {
			descriptor = SEURATPlugin.getImageDescriptor("Trade.gif");
		} else {
//				System.out.println("element.getType().toString()" + " not matched");
			descriptor = SEURATPlugin.getImageDescriptor("RatType.gif");
//				throw unknownElement(element);
		}
		
		//obtain the cached image corresponding to the descriptor
		Image image = (Image)g_rationaleElementIcons.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			g_rationaleElementIcons.put(descriptor, image);
		}
		return image;
	}
	/**
	 * Find the best icon to display for a given rationale element,
	 * if the image has not already been loaded it is placed into
	 * a cache so that subsequent uses of the image does not
	 * require additional loads, and also reduces memory overhead
	 * 
	 * @param pElement the rationale element whose icon should be retrieved
	 * @return an Image or null if no image could be found
	 */
	public static Image getRationaleElementIcon(RationaleElement pElement)
	{		
		return getRationaleElementIcon(pElement.getElementType(), null, pElement.getEnabled(), pElement.getName(), pElement);
	}
	
	private static Map<String, Image> g_RationaleErrorLevelIcons = new HashMap<String, Image>();
	static {
		g_RationaleErrorLevelIcons.put(
				(RationaleErrorLevel.ERROR).toString(), 
				SEURATPlugin.getImageDescriptor("error_tsk.gif").createImage()
		);
		g_RationaleErrorLevelIcons.put(
				(RationaleErrorLevel.WARNING).toString(), 
				SEURATPlugin.getImageDescriptor("warn_tsk.gif").createImage()
		);
		g_RationaleErrorLevelIcons.put(
				(RationaleErrorLevel.INFORMATION).toString(), 
				SEURATPlugin.getImageDescriptor("info_tsk.gif").createImage()
		);
		
	}

	public static Image getErrorLevelIcon(String pErrorLevel)
	{
		return g_RationaleErrorLevelIcons.get(pErrorLevel);		
	}
	public static Image getErrorLevelIcon(RationaleErrorLevel pErrorLevel)
	{
		return getErrorLevelIcon(pErrorLevel.toString());		
	}
}
