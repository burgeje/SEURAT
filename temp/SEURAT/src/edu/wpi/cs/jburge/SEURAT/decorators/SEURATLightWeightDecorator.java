package edu.wpi.cs.jburge.SEURAT.decorators;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.Logger;

/**
 * @author Administrator
 *
 * This method does the actual decorating. Like the other decorator files, it was adapted from a demo
 * (although I'm not sure who "Administrator" is). 
 */
public class SEURATLightWeightDecorator extends LabelProvider 
  implements ILightweightLabelDecorator 
{
	/** 
	  * Demo Image 
	  */
	 private static SEURATImages demoImage_ = new SEURATImages();

	 /**
	  * Boolean indicator to differenciate decoration at the start or not.. Not used
	  */
	 private static boolean newDecorationRequest_ = false;

	 /**
	  * List of decorators
	  */
	 private static List initialDecoratorList_ = new Vector();
  
	 private static boolean decorateTextLabels_ = true;
	 private static boolean decorateProject_ = true;

	 public static SEURATImages getDemoImage()
	 {
		 return demoImage_;
	 }
	 
	 public static boolean getNewDecorationRequest()
	 {
		 return newDecorationRequest_;
	 }
	 
	 public static List getInitialDecoratorList()
	 {
		 return initialDecoratorList_;
	 }
	 
	 public static boolean getDecorateTextLabels()
	 {
		 return decorateTextLabels_;
	 }
	 
	 public static boolean getDecorateProject()
	 {
		 return decorateProject_;
	 }

	/**
	 * Get the static instance of SEURATLightWeightDecorator
	 * 
	 * @return Demo decorator object
	 * 
	 */
	public static SEURATLightWeightDecorator getRatDecorator()
	{
	IDecoratorManager decoratorManager =
		SEURATPlugin.getDefault().getWorkbench().getDecoratorManager();
  
	  if (decoratorManager
		.getEnabled("edu.wpi.cs.jburge.SEURAT.decorators.ratdecorator"))
	  {
		return (SEURATLightWeightDecorator) decoratorManager.
	  getBaseLabelProvider
		("edu.wpi.cs.jburge.SEURAT.decorators.ratdecorator");
	  }
	  return null;
	}


	/**
	 * This is where the actual decoration takes place.
	 * @param object - this is what we are decorating
	 * @param decoration - this is what we are decorating it with
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object object, IDecoration decoration) 
	{
		IResource objectResource;
    
	// Get the resource using the adaptable mechanism. 
		objectResource = getResource(object);

		Vector decoratorImageKeys = new Vector();
		if (objectResource == null)
		{
	  // The IResource object is null for all the members / member methods etc
	  // for a java file in a package explorer. 
		  return;
		}
		
		// Decorating a Project
    
	// The project should be decorated with DecoratorDemo text label. 
		if (objectResource.getType() == IResource.PROJECT)
		{
		  // Projects should not be decorated
		  return;
		}

		// Decorating a Folder
		if (objectResource.getType() == IResource.FOLDER)
		{
		  // Folders should not be decorated..
		  return;
		}

	try
		{
		  // Resource properties have been changed. 

		  // Find the decorator with which the image should be decorated
		  decoratorImageKeys = SEURATResourcePropertiesManager.
		findDecorationImageForResource(objectResource);
			  
	  SEURATDecoratorManager.removeResource(objectResource);

	  if(decoratorImageKeys.size() != 0)
		  {
			   decoration.addOverlay(SEURATImages.ratDescriptor);
		  }
	  return;
		}
		catch (Exception e)
		{
		  Logger.logError (e);
		}
  }

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) 
  {
    
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() 
  {
	// Disposal of images present in the image registry can be performed in this
	// method
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) 
  {
	}
  
  /**
	 * Refresh the project. This is used to refresh the label decorators of
	 * of the resources. 
	 *
	 */
   public void refresh()
   {
	  newDecorationRequest_ = true;
	  initialDecoratorList_ = null;

	  List resourcesToBeUpdated;
    
	  // Get the Demo decorator 
	  SEURATLightWeightDecorator ratDecorator = getRatDecorator();
	  if (ratDecorator == null)
	  {
		return;
	  }
	  else
	  {
		resourcesToBeUpdated = SEURATDecoratorManager.getSuccessResources();
		// Fire a label provider changed event to decorate the 
		// resources whose image needs to be updated
      
		ratDecorator.fireLabelEvent(new LabelProviderChangedEvent(
		  ratDecorator, resourcesToBeUpdated.toArray()));
	  }
	}
  
	/** 
	 * Refresh all the resources in the project
	 */ 
	public void refreshAll(boolean displayTextLabel, boolean displayProject)
	{
	  decorateTextLabels_ = displayTextLabel;
	  decorateProject_ = displayProject;
 
	  SEURATLightWeightDecorator demoDecorator = getRatDecorator();
	  if (demoDecorator == null)
	  {
		return;
	  }
	  else
	  {
		demoDecorator.fireLabelEvent(new LabelProviderChangedEvent
		  (demoDecorator));
	  }
	}

	public void refresh(List resourcesToBeUpdated)
	{
	  newDecorationRequest_ = true;
	  initialDecoratorList_ = null;

	  SEURATLightWeightDecorator demoDecorator = getRatDecorator();
	  if (demoDecorator == null)
	  {
		return;
	  }
	  else
	  {
		// Fire a label provider changed event to decorate the 
		// resources whose image needs to be updated
		fireLabelEvent(
		  new LabelProviderChangedEvent(
			demoDecorator, resourcesToBeUpdated.toArray()));
	  }
	}

	/**
	 * Fire a Label Change event so that the label decorators are
	 * automatically refreshed.
	 * 
	 * @param event LabelProviderChangedEvent
	 */
	private void fireLabelEvent(final LabelProviderChangedEvent event)
	{
	// We need to get the thread of execution to fire the label provider
	// changed event , else WSWB complains of thread exception. 
	Display.getDefault().asyncExec(new Runnable()
	{
	  public void run()
	  {
		fireLabelProviderChanged(event);
	  }
	});
	}
    
  /**
	 * Returns the resource for the given input object, or
	 * null if there is no resource associated with it.
	 *
	 * @param object  the object to find the resource for
	 * @return the resource for the given object, or null
	 */
   private IResource getResource(Object object) 
   {
	 if (object instanceof IResource) 
	 {
	   return (IResource) object;
	 }
	 if (object instanceof IAdaptable) 
	 {
	   return (IResource) ((IAdaptable) object).getAdapter(IResource.class);
	 }
	 return null;
   }

}
