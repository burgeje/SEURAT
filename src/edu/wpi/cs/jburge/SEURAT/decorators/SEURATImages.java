package edu.wpi.cs.jburge.SEURAT.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

/**
 * Set of images that are used for decorating resources are maintained
 * here. This acts as a image registry and hence there is a single copy
 * of the image files floating around the project. Like the other decorator files, this
 * was originally based on an Eclipse demo (DemoImages.java). 
 * 
 */
public class SEURATImages
{
	/**
	 * Rat Image Descriptor
	 */ 
	public static final ImageDescriptor ratDescriptor = ImageDescriptor.
	createFromFile (SEURATLightWeightDecorator.class, "smallRat.gif");
	
	
	/**
	 * Constructor for SEURATImages.
	 */
	public SEURATImages()
	{
		super();
	}
	
	/**
	 * Get the rat image data
	 * 
	 * @return image data for the rationale indicator
	 */   
	public ImageData getRatImageData()
	{
		return ratDescriptor.getImageData();
	}
	
	
	
	/**
	 * Get the image data depending on the key
	 * 
	 * @return image data 
	 * 
	 */ 
	public ImageData getImageData(String imageKey)
	{
		if (imageKey.equals("Rat"))
		{
			return getRatImageData();
		}
		return null;
	}
	
	// public ImageDescriptor 
	
}
