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
