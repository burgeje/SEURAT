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

package edu.wpi.cs.jburge.SEURAT.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.ResultSet;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;

/**
 * This API is used when other programs wishes to navigate to a particular element in SEURAT
 * @author yechen
 *
 */
public class Navigator {

	public static final int EXPLORER_RATIONALE = 1;
	public static final int EXPLOERR_TACTIC = 3;
	public static final int EXPLORER_PATTERN = 2;

	/**
	 * Given the explorer id(definied as constants), element name and element type, navigate to
	 * the element in the explorer.
	 * @param explorerType
	 * @param elementName
	 * @param elementType
	 */
	public static void navigateTo(int explorerType, String elementName, RationaleElementType elementType){
		//Show View
		switch (explorerType){
		case EXPLORER_RATIONALE:
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer");
				RationaleExplorer exp = RationaleExplorer.getHandle();
				exp.showRationaleNode(elementName, elementType);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			break;

			//TODO
		}


	}

	/**
	 * Given the xmi-id of a pattern element, navigate to it in the rationale explorer.
	 * @param xmiID
	 * @return
	 */
	public static boolean navigateTo(String xmiID){
		PatternElement patternElement = new PatternElement();
		patternElement.fromDatabase(xmiID);
		if (patternElement.getID() <= 0) {
			//Is this a package?
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();
			
			try{
				Statement stmt = conn.createStatement();
				String query = "SELECT * FROM DIAGRAM_ALTERNATIVE WHERE package_xmi_id = '"
					+ RationaleDBUtil.escape(xmiID) + "'";
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()){
					Alternative alt = new Alternative();
					alt.fromDatabase(rs.getInt("alt_id"));
					if (alt.getID() < 0) return false;
					navigateTo(EXPLORER_RATIONALE, alt.getName(), RationaleElementType.ALTERNATIVE);
					return true;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			return false;
		}
		Alternative alt = new Alternative();
		alt.fromDatabase(patternElement.getAltID());
		if (alt.getID() < 0) {
			return false;
		}
		navigateTo(EXPLORER_RATIONALE, alt.getName(), RationaleElementType.ALTERNATIVE);
		return true;
	}
}
