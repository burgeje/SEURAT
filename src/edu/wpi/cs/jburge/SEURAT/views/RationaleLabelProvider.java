package edu.wpi.cs.jburge.SEURAT.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Provides the .gif files that are used to draw the images on the rationale
 * tree.
 * @author burgeje
 *
 */
public class RationaleLabelProvider extends LabelProvider {	
	private Map<ImageDescriptor,Image> imageCache = new HashMap<ImageDescriptor, Image>(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	/**
	 * Depending on what kind of element, find the right icon
	 * @param our node
	 * @return the image
	 */
	public Image getImage(Object obj) {
		TreeParent element;
		if (obj instanceof TreeParent)
		{
			element = (TreeParent)obj;
		}
		else
		{
			System.out.println("not a tree parent?");
			return null;
		}
		ImageDescriptor descriptor = null;
		if (element.getType() == RationaleElementType.REQUIREMENT) {
			if (element.getStatus() == RationaleErrorLevel.ERROR)
			{
				if (element.getActive())
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_error.gif");
				}
				else
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_error_Disabled2.gif");
					
				}
			}	 
			else if  (element.getStatus() == RationaleErrorLevel.WARNING)
			{
				if (element.getActive())
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
				if (element.getActive())
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq.gif");
				}
				else
				{
					descriptor = SEURATPlugin.getImageDescriptor("newReq_Disabled2.gif");				
				}
			}
		} else if (element.getType() == RationaleElementType.DECISION) {
			if (element.getStatus() == RationaleErrorLevel.ERROR)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec_error.gif");
			}	 
			else if  (element.getStatus() == RationaleErrorLevel.WARNING)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec_warning.gif");		
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newDec.gif");
			}
		} else if (element.getType() == RationaleElementType.ALTERNATIVE) {
			if (element.getStatus() == RationaleErrorLevel.ERROR)
			{
				if (element.getActive())
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel_error.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_error.gif");
			}	 
			else if  (element.getStatus() == RationaleErrorLevel.WARNING)
			{
				if (element.getActive())
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel_warning.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_warning.gif");
			}
			else
			{
				if (element.getActive())
					descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel.gif");
				else	
					descriptor = SEURATPlugin.getImageDescriptor("newAlt.gif");
				
			}
		} else if (element.getType() == RationaleElementType.ARGUMENT) {
			//arguments require a bit more processing since we want to 
			//differentiate between arguments for and against
			Argument ourArg = (Argument) RationaleDB.getRationaleElement(element.getName(), RationaleElementType.ARGUMENT);
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
		} else if (element.getType() == RationaleElementType.CLAIM) {
			descriptor = SEURATPlugin.getImageDescriptor("newClaim2.gif");
		} else if (element.getType() == RationaleElementType.ALTCONSTREL)
		{
			descriptor = SEURATPlugin.getImageDescriptor("Constr_Rel.gif");
		} else if (element.getType() == RationaleElementType.QUESTION) {
			if (element.getActive())
			{
				descriptor = SEURATPlugin.getImageDescriptor("newQuest.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newQuest_Unanswered.gif");
			}
			
			
		} else if (element.getType() == RationaleElementType.COOCCURRENCE) {
			descriptor = SEURATPlugin.getImageDescriptor("Cooc.gif");
		} else if (element.getType() == RationaleElementType.ONTENTRY) {
			descriptor = SEURATPlugin.getImageDescriptor("newOnt.gif");
		} 
		else if (element.getType() == RationaleElementType.DESIGNPRODUCTENTRY) {
			descriptor = SEURATPlugin.getImageDescriptor("product.gif");
		}
		else if (element.getType() == RationaleElementType.EXPERTISE) {
			descriptor = SEURATPlugin.getImageDescriptor("product.gif");
		}
		else if (element.getType() == RationaleElementType.CONSTRAINT)
		{
			descriptor = SEURATPlugin.getImageDescriptor("constraint.gif");
		}
		else if (element.getType() == RationaleElementType.CONTINGENCY)
		{
			descriptor = SEURATPlugin.getImageDescriptor("claim.gif");
		}
		else if (element.getType() == RationaleElementType.DESIGNER)
		{
			descriptor = SEURATPlugin.getImageDescriptor("designer.gif");
		}
		else if (element.getType() == RationaleElementType.ASSUMPTION) 
		{
			if (element.getActive())
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAssump.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAssump_Disabled.gif");
			}
			
		} else if (element.getType() == RationaleElementType.RATIONALE) {
			descriptor = SEURATPlugin.getImageDescriptor("Rat2.gif");
		} else if (element.getType() == RationaleElementType.TRADEOFF) {
			descriptor = SEURATPlugin.getImageDescriptor("Trade.gif");
		} else {
//			System.out.println("element.getType().toString()" + " not matched");
			descriptor = SEURATPlugin.getImageDescriptor("RatType.gif");
//			throw unknownElement(element);
		}
		
		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
	
	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object obj) {
		TreeParent element;
		if (obj instanceof TreeParent)
		{
			element = (TreeParent)obj;
		}
		else
		{
			return null;
		}
		return element.getName();
	}
	
	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}
	
	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}
	
}
