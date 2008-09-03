/*
 * Created on Jan 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.*;

import edu.wpi.cs.jburge.SEURAT.queries.CommonArgument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Handles inferencing (evaluation, error checking...) when the argument or
 * someone associated with it is modified.
 * @author jburge
 */
public class ArgumentInferences {
	
	
	/**
	 * Empty constructor - calls superclass
	 *
	 */
	public ArgumentInferences() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Updates an argument when a related rationale element is modified/deleted
	 * @param arg - our argument
	 * @param deleting - true if the update is the argument being deleted
	 * @return a vector containing new status information to be displayed
	 */
	public Vector<RationaleStatus> updateArgument(Argument arg, boolean deleting)
	{
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		//boolean error = false;
		try {
			stmt = conn.createStatement();
			
			RationaleElementType argParent = arg.getPtype();
			
			//if we are deleting the argument, need to update any
			//related requirements *or* alternatives!
			if (deleting)
			{
				if (arg.getRequirement()!= null)
				{
					RequirementInferences reqI = new RequirementInferences();
					reqI.updateRequirement(arg.getRequirement());
				}
				else if (arg.getAlternative() != null)
				{
					AlternativeInferences altI = new AlternativeInferences();
					altI.updateAlternative(arg.getAlternative());
				}
			}
			
			//so who are we arguing for?
			if (argParent == RationaleElementType.ALTERNATIVE)
			{
				AlternativeInferences inf = new AlternativeInferences();
				String findAltQuery = "Select name From alternatives where " +
				"id = " + arg.getParent();
//				***				System.out.println(findAltQuery);
				rs = stmt.executeQuery(findAltQuery);
				
				while (rs.next())
				{
					String altName = RationaleDBUtil.decode(rs.getString("name"));
					Alternative alt = new Alternative();
					alt.fromDatabase(altName);
					newStatus.addAll(inf.updateAlternative(alt));
				}
			}
			//requirement
			else if (argParent == RationaleElementType.REQUIREMENT)
			{
				RequirementInferences inf = new RequirementInferences();
				String findReqQuery = "Select name from requirements where " +
				"id = " + arg.getParent();
//				***				System.out.println(findReqQuery);
				rs = stmt.executeQuery(findReqQuery);
				
				while (rs.next())
				{
					String reqName = RationaleDBUtil.decode(rs.getString("name"));
					Requirement req = new Requirement();
					req.fromDatabase(reqName);
					newStatus.addAll(inf.updateRequirement(req));
				}
			}
			
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "ArgumentInferences.updateArgument", "Check both queries");
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(arg.getID(), arg.getName(), RationaleElementType.ARGUMENT);
		
		return newStatus;			 
		
	}
	
	/**
	 * Used in by the common argument display to show which arguments appear
	 * the most often. This returns a vector of arguments that match the type
	 * passed in. If selected is true, only arguments for selected alternatives
	 * are passed in. 
	 * @param type - the type of argument - argument, claim, ontology entry
	 * @param selected - true if we only care about arguments for selected alternatives
	 * @return vector of type "CommonArgument" which indicates argument, total count, count for and against
	 */
	public Vector<CommonArgument> argumentStatistics(RationaleElementType type, boolean selected)
	{
		Vector<CommonArgument> commonArgVector = new Vector<CommonArgument>();
		Hashtable<String,CommonArgument> commonArgs = new Hashtable<String,CommonArgument>();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		ResultSet rs2 = null;
		String findArgQuery = "";
		//boolean error = false;
		try 
		{
			stmt = conn.createStatement();
			String searchType;
			if (type == RationaleElementType.ONTENTRY)
			{
				searchType = RationaleElementType.CLAIM.toString();;
			}
			else
			{
				searchType = type.toString();
			}
			findArgQuery = "Select name From arguments where " +
			"argtype = '" + searchType + "'";
//			***			System.out.println(findArgQuery);
			rs = stmt.executeQuery(findArgQuery);
			
			while (rs.next())
			{
				String argName = RationaleDBUtil.decode(rs.getString("name"));
				Argument arg = new Argument();
				arg.fromDatabase(argName);
				
				boolean countThis = true;
				if (selected)
				{
					if (arg.getPtype() != RationaleElementType.ALTERNATIVE)
					{
						countThis = false;
					}
					else
					{
						Alternative alt = new Alternative();
						alt.fromDatabase(arg.getParent());
						if (alt.getStatus() != AlternativeStatus.ADOPTED)
						{
							countThis = false;
						}
						/*						String findAltQuery = "Select * from alternatives where " +
						 " id = " + arg.getParent() + " and status = '" + 
						 AlternativeStatus.ADOPTED + "'";
						 System.out.println(findAltQuery);
						 rs2 = stmt.executeQuery(findAltQuery);
						 
						 if (!(rs2.next()))
						 {
						 countThis = false;
						 }
						 //							rs2.close();
						  */
					}
					
				} //check for selected
				if (countThis)
				{
					String cArgName;
					if (type == RationaleElementType.ASSUMPTION)
					{
						cArgName = arg.getAssumption().getName();
					}
					else if (type == RationaleElementType.CLAIM)
					{
						if (arg.getClaim() != null)
							cArgName = arg.getClaim().getName();
						else
						{
							System.out.println("claim was null??");
							System.out.println(argName);
							cArgName = "reduced coupling";
						}
						
					}
					else
					{
						if (arg.getClaim() != null)
							cArgName = arg.getClaim().getOntology().getName();
						else
						{
							System.out.println("claim was null??");
							System.out.println(argName);
							cArgName = "missing ontology";
						}
					}
					CommonArgument ca;
					ca = (CommonArgument) commonArgs.get(cArgName);
					if (ca == null)
					{
						ca = new CommonArgument();
						ca.setArgumentName(cArgName);
						ca.setType(type);
						commonArgs.put(cArgName, ca);						
					}
					if (arg.getType() == ArgType.SUPPORTS)
					{
						ca.incrementFor();
					}
					else if (arg.getType() == ArgType.DENIES)
					{
						ca.incrementAgainst();
					}
					
				}
			}
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Argument Inferences.argumentStatistics",
					findArgQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs, rs2);
		}
		
		commonArgVector.addAll(commonArgs.values());
		return commonArgVector;			 
		
	}
	
	
}
