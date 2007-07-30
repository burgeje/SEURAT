/*
 * Created on Jan 5, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.Enumeration;

/**
 * Another unused file. This was an attempt to define the enumerated type
 * as an interface. The enumerated types all contain a lot of duplicated
 * code and there have been several attempts to inherit it from somewhere else.
 * @author jburge
 */
public interface IEnumeratedType {
	Enumeration elements();
	String toString(); 
	int size();
	IEnumeratedType first();
	IEnumeratedType last();
	IEnumeratedType prev();
	IEnumeratedType next();
	abstract IEnumeratedType fromString(String rt);

}
