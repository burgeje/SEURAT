package edu.wpi.cs.jburge.SEURAT.api;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * We had a request for a SEURAT API. This was an experiment along those lines to see if we could expose
 * part of the API sucessfully. Nothing else happened with this though. 
 * @author burgeje
 *
 */
public class RationaleNode {

	
	private static final long serialVersionUID = 1L;
	// class variables
	// instance variables
	
	/**
	 * Unique id
	 */
	private int id;
	/**
	 * Element name
	 */
	private String name;
	
	/**
	 * Element description
	 */
	private String description;
	
	/**
	 * Type of rationale element (requirement, decision, etc.)
	 */
	private RationaleElementType type;
	
	/**
	 * Enabled flag - false if disabled
	 */
	boolean enabled;

	/**
	 * Constructor - currently just creates a test node.
	 *
	 */
	public RationaleNode()
	{
		name = "Rationale Base";
		description = "This is a placeholder for a tree root";
		type = RationaleElementType.RATIONALE;
		enabled = true;
	}
	
	/**
	 * Creates a RationaleNode from a RationaleElement
	 * @param element
	 */
	RationaleNode(RationaleElement element)
	{
		id = element.getID();
		name = element.getName();
		description = element.getDescription();
		enabled = element.getEnabled();
		type = element.getElementType();
	} 
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RationaleElementType getType() {
		return type;
	}
	public void setType(RationaleElementType type) {
		this.type = type;
	}
	public void setId(int number)
	{
		id = number;
	}
	public int getId()
	{
		return id;
	}
}
