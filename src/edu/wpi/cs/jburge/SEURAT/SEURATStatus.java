package edu.wpi.cs.jburge.SEURAT;

import org.eclipse.core.runtime.Status;

/**
 * Creates a status element from an exception. This does not appear to
 * really be used for anything useful and is probably yet another example
 * of code that came along with a demo example.
 * @author burgeje
 *
 */
public class SEURATStatus extends Status
{
	public SEURATStatus(
			int type,
			int code,
			String message,
			Throwable exception)
	{
		super(type, "hello", code, message, exception);
	}
	
	public SEURATStatus(int code, String message)
	{
		this(code, code, message, null);
	}
	
	public SEURATStatus(int code, String message, Throwable exception)
	{
		this(code, code, message, exception);
	}
}
