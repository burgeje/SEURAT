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
