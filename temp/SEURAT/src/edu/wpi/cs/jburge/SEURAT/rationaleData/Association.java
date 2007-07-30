/*
 * Created on Nov 6, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.rationaleData;

/**
 * Contains the information associating an alternative with the code that
 * implements it. This class is used, but it isn't saved and restored to
 * the database like other elements. It also contains less information than is
 * stored in the database!
 * @author jburge
 */
public class Association {
	
	/**
	 * The alternative
	 */
	int alt;
	/**
	 * The resource (file)
	 */
	String resource;
	/**
	 * The line number in the file 
	 */
	int lineNumber;
	/**
	 * The message displayed indicating the alternative associated
	 */
	String msg;
	/**
	 * 
	 */
	public Association() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return int
	 */
	public int getAlt() {
		return alt;
	}
	
	/**
	 * @return int
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * @return String
	 */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * @return String
	 */
	public String getResource() {
		return resource;
	}
	
	/**
	 * Sets the alt.
	 * @param alt The alt to set
	 */
	public void setAlt(int alt) {
		this.alt = alt;
	}
	
	/**
	 * Sets the lineNumber.
	 * @param lineNumber The lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	/**
	 * Sets the msg.
	 * @param msg The msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	/**
	 * Sets the resource.
	 * @param resource The resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
	
}
