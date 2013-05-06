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
		else if (type == RationaleElementType.CANDIDATE)
		{
			return new CandidateRationale(RationaleElementType.NONE);
		}
		else if (type == RationaleElementType.PATTERN)
		{
			return new Pattern();
		}
		else if (type == RationaleElementType.PATTERNDECISION)
		{
			return new PatternDecision();
		}
		else if (type == RationaleElementType.ALTERNATIVEPATTERN)
		{
			return new AlternativePattern();
		}
		else
		{
			System.out.println("Couldn't create item!!!");
			return null; //shouldn't happen!
		}
	}
	
}
