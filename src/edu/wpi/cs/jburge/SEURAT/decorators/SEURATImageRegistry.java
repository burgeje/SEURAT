package edu.wpi.cs.jburge.SEURAT.decorators;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Image registry for Resources. This class is a utility class to get
 * the image given the image key.  This was also taken from an Eclipse demo and was
 * originally named DemoImageRegistry.
 * 
 * @author balajik
 *

 */ 
public class SEURATImageRegistry
{
	/**
	 * The image registry containing the map of images to keys
	 */
	private ImageRegistry imageRegistry;
	
	/**
	 * Constructor for SEURATImageRegistry.
	 */
	public SEURATImageRegistry()
	{
		if (imageRegistry == null)
		{
			imageRegistry = new ImageRegistry();
		}
	}
	
	/**
	 * Get the image from image registry given the key
	 *
	 * @param key Image key
	 * @return Image
	 */  
	public Image getImage(String key)
	{
		return imageRegistry.get(key);
	}
	
	/**
	 * Assosiate the image with image key
	 * 
	 * @param key Image key
	 * @param image Image to be assosiated with image key
	 * 
	 */ 
	public void setImage(String key, Image image)
	{
		imageRegistry.put(key, image);
	}
}
