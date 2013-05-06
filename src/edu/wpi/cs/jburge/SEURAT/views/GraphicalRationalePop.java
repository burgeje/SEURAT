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

package edu.wpi.cs.jburge.SEURAT.views;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgCategory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;

/**
 * Draws the rationale tree graphically on a popup shell.
 * @author Ryan Wagner
 *
 */
public class GraphicalRationalePop {
	/**
	 * The distance between an icon and it's label.
	 */
	public static final int LABEL_MARGIN = 20;
	/**
	 * The required offset to make a line appear to come from the center
	 * of an icon.
	 */
	public static final int LINE_ADJUST = 8;
	/**
	 * The spacing between two icons on the same level
	 */
	public static final int SAMEROW_SPACE = 100;
	
	/**
	 * The popup shell
	 */
	private Shell shell;
	/**
	 * The display
	 */
	private Display display;
	/**
	 * A database of the locations of all the icons already drawn
	 */
	private HashMap<String,Point> pointDB;

	/**
	 * The drawing instrument
	 */
	private GC gc;
	
	/**
	 * Sets up and draws the graphical rationale web on a new shell
	 * @param parent
	 */
	public GraphicalRationalePop(Composite parent){
		pointDB = new HashMap<String,Point>();
		shell = new Shell();
		shell.setSize(750, 750);
		shell.open();
		gc = new GC(shell);

		RationaleDB db = RationaleDB.getHandle();
		
		int currentX = 0;
		int currentY = 0;
		//draw Requirements and children
		Vector<Requirement> reqs = db.getEnabledRequirements(true);
		Requirement currentReq;
		String currentArg;
		Vector<String> argsFor;
		Vector<String> argsAgainst;
		int childDifferX = 50;
		int childDifferY = 30;
		ArgCategory argCat;
		for(int i=0; i< reqs.size();i++){
			currentReq = reqs.get(i);
			currentX+=SAMEROW_SPACE;
			currentY+=40;
			drawIcon(currentReq,"newReq.gif",currentX,currentY);
			argsFor = currentReq.getArgumentsFor();
			argsAgainst = currentReq.getArgumentsAgainst();
			if(argsFor.size() > 0){
				for(int j=0; j< argsFor.size(); j++){
					currentArg = argsFor.get(j);
					drawIcon(currentArg,"argFor.gif",currentX-childDifferX,currentY+childDifferY);
					//childDifferX += 20;
					childDifferY += 30;
					drawLineBetween(currentArg, "argFor.gif", currentReq.getName(), "newReq.gif");
					//getArguments* is broken
//					argCat = currentArg.getCategory();
//					if(argCat == ArgCategory.ALTERNATIVE){
//						drawIcon(currentArg.getAlternative(),"newAlt.gif",currentX-childDifferX,currentY-childDifferY-20);
//					}
				}
			}
			if(argsAgainst.size() > 0){
				for(int j=0; j< argsAgainst.size(); j++){
					currentArg = argsAgainst.get(j);
					drawIcon(currentArg,"argAgainst.gif",currentX-childDifferX,currentY+childDifferY);
					//childDifferX += 20;
					childDifferY += 30;
					drawLineBetween(currentArg, "argAgainst.gif", currentReq.getName(), "newReq.gif");
				}
			}
			childDifferX = 50;
			childDifferY = 30;
		}
		Vector<Decision> decs = db.getAllDecisions();
		currentX = 0;
		currentY += 100;
		Decision currentDec;
		Alternative currentAlt;
		Vector<Alternative> alts;
		Vector<Argument> dargsFor;
		Vector<Argument> dargsAgainst;
		Argument dcurrentArg;
		for(int i=0; i< decs.size(); i++){
			currentDec = decs.get(i);
			currentX+=SAMEROW_SPACE;
			currentY+=40;
			drawIcon(currentDec,"newDec.gif",currentX,currentY);
			alts = currentDec.getAlternatives();
			for(int j=0;j<alts.size();j++){
				currentAlt = alts.get(j);
				drawIcon(currentAlt,"newAlt.gif",currentX-childDifferX,currentY+childDifferY);
				drawLineBetween(currentAlt,"newAlt.gif",currentDec,"newDec.gif");
				childDifferY += 30;
				dargsAgainst = currentAlt.getArgumentsAgainst();
				dargsFor = currentAlt.getArgumentsFor();
				Requirement argReq;
				if(dargsFor.size() > 0){
					for(int k=0; k< dargsFor.size(); k++){
						dcurrentArg = dargsFor.get(k);
						drawIcon(dcurrentArg,"argFor.gif",currentX-childDifferX,currentY+childDifferY);
						childDifferX -= 20;
						childDifferY += 30;
						drawLineBetween(dcurrentArg, "argFor.gif", currentAlt, "newAlt.gif");
						argReq = dcurrentArg.getRequirement();
						if(argReq != null){
							drawLineBetween(dcurrentArg,"argFor.gif", argReq,"newReq.gif");
						}
					}
				}
				if(dargsAgainst.size() > 0){
					for(int l=0; l< dargsAgainst.size(); l++){
						dcurrentArg = dargsAgainst.get(l);
						drawIcon(dcurrentArg,"argAgainst.gif",currentX-childDifferX,currentY+childDifferY);
						childDifferX -= 20;
						childDifferY += 30;
						drawLineBetween(dcurrentArg, "argAgainst.gif", currentAlt, "newAlt.gif");
						argReq = dcurrentArg.getRequirement();
						if(argReq != null){
							drawLineBetween(dcurrentArg,"argAgainst.gif", argReq,"newReq.gif");
						}
					}
				}
				childDifferY = 30;
			}
		}
		
		//drawIcon("words","newReq.gif",0,0);
		//drawIcon("wordsagain","newReq.gif",200,100);
		//drawLineBetween("words","newReq.gif","wordsagain","newReq.gif");
		gc.dispose();
	}
	/**
	 * Draws the given RationaleElement with the given graphic at the specified location.
	 * The graphic will be stored as the element's type. Each RationaleElement.name()/graphic pair must be unique
	 * @param ele
	 * @param graphic
	 * @param x
	 * @param y
	 */
	private void drawIcon(RationaleElement ele, String graphic, int x, int y){
		Image image = new Image(display,SEURATPlugin.getImageDescriptor(graphic).getImageData());
		gc.drawImage(image, x, y);
		gc.drawText(ele.getName(), x, y+LABEL_MARGIN);
		pointDB.put(ele.getName()+graphic, new Point(x,y));
		
	}
	/**
	 * Draws a new icon with the given graphic at the specified location with the name specified.
	 * The graphic will be stored as the element's type. Each name/graphic pair must be unique
	 * @param ele
	 * @param graphic
	 * @param x
	 * @param y
	 */
	private void drawIcon(String name, String graphic, int x, int y){
		Image image = new Image(display,SEURATPlugin.getImageDescriptor(graphic).getImageData());
		gc.drawImage(image, x, y);
		gc.drawText(name, x, y+LABEL_MARGIN);
		pointDB.put(name+graphic, new Point(x,y));
		
	}
	/**
	 * Draws a line between the first RationaleElement's icon and the second's icon.
	 * The types must be specified because a RationaleElement will not divulge it's type easily.
	 * @param first
	 * @param firstGraphic
	 * @param second
	 * @param secondGraphic
	 */
	private void drawLineBetween(RationaleElement first, String firstGraphic, RationaleElement second, String secondGraphic){
		Point f = pointDB.get(first.getName()+firstGraphic);
		Point s = pointDB.get(second.getName()+secondGraphic);
		gc.drawLine(f.x+LINE_ADJUST, f.y+LINE_ADJUST, s.x+LINE_ADJUST, s.y+LINE_ADJUST);
	}
	/**
	 * Draws a line between the icon with the specified name and type with the second.
	 * @param first
	 * @param firstGraphic
	 * @param second
	 * @param secondGraphic
	 */
	private void drawLineBetween(String first, String firstGraphic, String second, String secondGraphic){
		Point f = pointDB.get(first+firstGraphic);
		Point s = pointDB.get(second+secondGraphic);
		gc.drawLine(f.x+LINE_ADJUST, f.y+LINE_ADJUST, s.x+LINE_ADJUST, s.y+LINE_ADJUST);
	}
}

