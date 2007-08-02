/*
 * Created on Mar 25, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.rationaleData;

/**
 * This class implements a paramerized factory method - it is
 * given a parameter, in this case the RationaleElementType, and
 * returns the correct subclass of RationaleElement.
 * @author jburge
 */
public final class RationaleElementFactory {
	
	/**
	 * 
	 */
	public RationaleElementFactory() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * The paramterized factory method
	 * @param type - the type of RationaleElement we need
	 * @return our new RationaleElement
	 */
	public static RationaleElement getRationaleElement(RationaleElementType type)
	{
		if (type == RationaleElementType.REQUIREMENT)
		{
			return new Requirement();
		}
		else if (type == RationaleElementType.ALTERNATIVE)
		{
			return new Alternative();
		}
		else if (type == RationaleElementType.ARGUMENT)
		{
			return new Argument();
		}
		else if (type == RationaleElementType.ASSUMPTION)
		{
			return new Assumption();
		}
		else if (type == RationaleElementType.CLAIM)
		{
			return new Claim();
		}
		else if (type == RationaleElementType.EXPERTISE)
		{
			return new AreaExp();
		}
		else if (type == RationaleElementType.ALTCONSTREL)
		{
			return new AltConstRel();
		}
		else if (type == RationaleElementType.COOCCURRENCE)
		{
			return new Tradeoff(false);
		}
		else if (type == RationaleElementType.DECISION)
		{
			return new Decision();
		}
		else if (type == RationaleElementType.ONTENTRY)
		{
			return new OntEntry();
		}
		else if (type == RationaleElementType.DESIGNPRODUCTENTRY)
		{
			return new DesignProductEntry();
		}
		else if (type == RationaleElementType.CONSTRAINT)
		{
			return new Constraint();
		}
		else if (type == RationaleElementType.CONTINGENCY)
		{
			return new Contingency();
		}
		else if (type == RationaleElementType.DESIGNER)
		{
			return new Designer();
		}
		else if (type == RationaleElementType.QUESTION)
		{
			return new Question();
		}
		else if (type == RationaleElementType.TRADEOFF)
		{
			return new Tradeoff(true);
		}
		else
		{
			System.out.println("Couldn't create item!!!");
			return null; //shouldn't happen!
		}
	}
	
}
