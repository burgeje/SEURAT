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
