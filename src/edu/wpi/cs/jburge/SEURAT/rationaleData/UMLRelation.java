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

package edu.wpi.cs.jburge.SEURAT.rationaleData;

public final class UMLRelation {
	
	/**
	 * Number of relations in this interface.
	 */
	public static final int NUMRELATIONS = 4;
	
	/**
	 * An association is bidirectional relation in this model.
	 * Placeholder for the traditional "dashed line" support on collaborations.
	 */
	public static final int ASSOCIATION = 0;
	
	/**
	 * On participants. One participant is aggregating another if it has some multiplicity of
	 * the other object in it.
	 * Source <>---- Target
	 */
	public static final int AGGREGATION = 1;
	
	public static final int INV_AGGREGATION = -1;
	
	/**
	 * Delegation is used on operations or participants
	 * One operation deferring the implementation of another operation in some other participant.
	 */
	public static final int DELEGATION = 2;
	
	public static final int INV_DELEGATION = -2;
	
	/**
	 * Source (is a) --|> Target
	 */
	public static final int GENERALIZATION = 3;
	
	public static final int INV_GENERALIZATION = -3;
	
}
