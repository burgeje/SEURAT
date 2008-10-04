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
 * Provides the .gif files that are used to draw the images on the candidate rationale
 * tree.
 * @author burgeje
 *
 */
public class CandidateLabelProvider extends LabelProvider {	
	private Map<ImageDescriptor,Image> imageCache = new HashMap<ImageDescriptor, Image>(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	/**
	 * Depending on what kind of element, find the right icon
	 * @param obj our node
	 * @return the image
	 */
	public Image getImage(Object obj) {
		CandidateTreeParent element;
		if (obj instanceof CandidateTreeParent)
		{
			element = (CandidateTreeParent)obj;
		}
		else
		{
			System.out.println("not a tree parent?");
			return null;
		}
		ImageDescriptor descriptor = null;
		System.out.println(element.getType().toString());
		if (element.getType() == RationaleElementType.REQUIREMENT) {
			
			descriptor = SEURATPlugin.getImageDescriptor("newReq.gif");
			
		} else if (element.getType() == RationaleElementType.DECISION) {
			descriptor = SEURATPlugin.getImageDescriptor("newDec.gif");
		} else if (element.getType() == RationaleElementType.QUESTION) {
			descriptor = SEURATPlugin.getImageDescriptor("newQuest.gif");
		} else if (element.getType() == RationaleElementType.ASSUMPTION) {
			descriptor = SEURATPlugin.getImageDescriptor("newAssump.gif");
		}
		else if (element.getType() == RationaleElementType.ALTERNATIVE){
			//We need to actually look at the element to find the right icon
			CandidateRationale ourEle = (CandidateRationale) RationaleDB.getRationaleElement(element.getName(), RationaleElementType.CANDIDATE);
			if (ourEle.getQualifier() == null)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAlt.gif");
			}
			else if (AlternativeStatus.fromString(ourEle.getQualifier()) == AlternativeStatus.ADOPTED)
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAlt_Sel.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newAlt.gif");
			}
			
			
		} else if (element.getType() == RationaleElementType.ARGUMENT) {
			//We need to actually look at the element to find the right icon
			CandidateRationale ourEle = (CandidateRationale) RationaleDB.getRationaleElement(element.getName(), RationaleElementType.CANDIDATE);
			if (ourEle.getQualifier() == null)
			{		
			descriptor = SEURATPlugin.getImageDescriptor("newArg.gif");
			}
			else if (ArgType.fromString(ourEle.getQualifier()) == ArgType.DENIES)
			{
				descriptor = SEURATPlugin.getImageDescriptor("argAgainst.gif");
			}
			else if (ArgType.fromString(ourEle.getQualifier()) == ArgType.SUPPORTS)
			{
				descriptor = SEURATPlugin.getImageDescriptor("argFor.gif");
			}
			else
			{
				descriptor = SEURATPlugin.getImageDescriptor("newArg.gif");
			}
			
		}
		else if (element.getType() == RationaleElementType.RATIONALE){
//			System.out.println("element.getType().toString()" + " not matched");
			descriptor = SEURATPlugin.getImageDescriptor("Rat.gif");
//			throw unknownElement(element);
		}
		else
		{
			descriptor = SEURATPlugin.getImageDescriptor("Rat.gif");		
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
		CandidateTreeParent element;
		if (obj instanceof CandidateTreeParent)
		{
			element = (CandidateTreeParent)obj;
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
