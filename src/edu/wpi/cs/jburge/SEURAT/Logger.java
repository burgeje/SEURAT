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

package edu.wpi.cs.jburge.SEURAT;


/**
 * Simple Logger utility to log information
 */ 

import org.eclipse.core.runtime.Status;

/**
 * Called by the Decorator code when there is a problem. Do we really need this or 
 * is this code just something that came from an on-line example?
 * @author burgeje
 *
 */
public class Logger
{
	/**
	 * Creates a status item with a type ERROR. 
	 * @param message - an error message
	 * @param throwable - an exception
	 */
	public static void logError(String message, Throwable throwable)
	{
		SEURATPlugin.getDefault().getLog().log(
				new SEURATStatus(Status.ERROR, message, throwable));
	}
	
	/**
	 * Creates a status item with a type error given just the exception
	 * @param throwable - an exception
	 */
	public static void logError(Throwable throwable)
	{
		SEURATPlugin.getDefault().getLog().log(
				new SEURATStatus(Status.ERROR, throwable.getMessage(), throwable));
	}  
	
	/**
	 * Creates an information status item
	 * @param message - an information message
	 */
	public static void logInfo(String message)
	{
		SEURATPlugin.getDefault().getLog().log(
				new SEURATStatus(Status.INFO, message));
	}  
}
