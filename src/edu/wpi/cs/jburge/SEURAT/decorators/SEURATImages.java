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
