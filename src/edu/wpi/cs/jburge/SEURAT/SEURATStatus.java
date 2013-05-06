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
