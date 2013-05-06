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

package edu.wpi.cs.jburge.SEURAT;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.wpi.cs.jburge.SEURAT.decorators.SEURATDecoratorManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Association;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * This class manages the persistence of SEURAT associations through resource
 * changes such as refactoring.  It implements the JDT element change listener
 * class so that when a Java element is changed the elementChanged method is called with
 * the element change event passed.  This allows us to get the elements
 * that were affected by the resource change.  From there, we determine if they are associated
 * artifacts and update them if they are.
 * 
 * While this functionality was implemented as part of the multi-user rationale project,
 * it also has a use in single-user rationale, because the rationale markers do not
 * update by themselves for some types of refactoring (such as moving a method).  (Per request,
 * ratmarkers are no longer persistent, so this is now essential for single-user rationale)
 * 
 * @author molerjc
 * 
 */
public class SEURATElementChangedManager implements IElementChangedListener {
	
	/**
	 * Stores the changes to java elements that have occurred.
	 */
	protected ArrayList<IJavaElementDelta> relevantChanges;
	
	/**
	 * IJavaElementDelta for a saved delta.  When a method or similar
	 * lower-level artifact is renamed or moved, there is a remove and an add delta
	 * generated.  We need both of them to track where the element has gone.
	 */
	protected IJavaElementDelta savedDelta = null;
	
	/**
	 * Saved association type for the corresponding saved delta.
	 */
	protected Association savedAssoc = null;
	
	/**
	 * Defines the types of java element deltas that we are interested in.
	 */
	int flagChecks = (IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_MODIFIERS |
					IJavaElementDelta.F_MOVED_TO | IJavaElementDelta.F_MOVED_FROM |
					IJavaElementDelta.F_CHILDREN);
	
	
	/**
	 * This method is called when an element changed event occurs.  It first
	 * calls navigateModel to get the changes, and then iterates through the
	 * changes and calls handleChange for each one.
	 * 
	 * @param event - the ElementChangedEvent
	 */
	public void elementChanged(ElementChangedEvent event) {
		System.out.println("in event");
		IJavaElementDelta delta = event.getDelta(); // This will be the java model
		relevantChanges = new ArrayList<IJavaElementDelta>();
		navigateModel(delta); // Get the possible artifacts that have changed
		Iterator<IJavaElementDelta> changeIter = relevantChanges.iterator();
		while (changeIter.hasNext()) {
			IJavaElementDelta change = changeIter.next();
			if (relevantChanges.size() % 2 == 1 && (change.getFlags() & IJavaElementDelta.F_MOVED_TO) == 0) {
				// Don't consider changes that are just a single element being added/removed
				// One drawback of the system manifests here- the association needs to be manually removed
				// before deleting the element it's associated with, but unlikely to come up in practice.
				System.out.println("not considering single add/remove change");
			} else {
				handleChange(change);
				changeIter.remove();
			}
		}
	}
	
	/**
	 * Recursive method that navigates the Java model and adds all of the
	 * changes that occurred to possible associated artifacts.  We won't
	 * know whether they are actually associated until we check the database
	 * in the handleChange method.
	 * 
	 * Note: There are four types of changes we're interested in.
	 * Change to method inside class- fine grained removed and added method
	 * Moved method- fine grained removed and added method
	 * Change to class- non-fine grained removed and added compilation unit with moved from/to attributes set
	 * Change to class with a method inside- same as above, we take care of it in handleChange
	 * 
	 * @param delta - the current IJavaElementDelta
	 */
	private void navigateModel(IJavaElementDelta delta) {
		IJavaElementDelta[] deltas = delta.getAffectedChildren();
		for (int i = 0; i < deltas.length; i++) {
			int type = deltas[i].getElement().getElementType();
			int flags = deltas[i].getFlags();
			if (type == IJavaElement.COMPILATION_UNIT && (flags & flagChecks) != 0) {
				System.out.println("NavigateModel: " + deltas[i].getElement() + " " + deltas[i].toString() + " " + deltas[i].getKind());
				if ((flags & IJavaElementDelta.F_FINE_GRAINED) == 0 &&
						(deltas[i].getKind() == IJavaElementDelta.CHANGED ||
						deltas[i].getKind() == IJavaElementDelta.ADDED)) {
					// change was to a compilation unit and there were no finer grained changes
					//relevantChanges.add(deltas[i]);
				} else if (deltas[i].getKind() != IJavaElementDelta.CHANGED &&
						deltas[i].getMovedToElement() != null) {
					relevantChanges.add(deltas[i]);
				}
			} else if (type > IJavaElement.COMPILATION_UNIT) {
				System.out.println("NavigateModel: " + deltas[i].getElement() + " " + deltas[i].toString() + " " + deltas[i].getKind());
				// change was to an element of a compilation unit, such as a method
				relevantChanges.add(deltas[i]);
			}
			navigateModel(deltas[i]);
		}
	}
	
	/**
	 * Determines whether a change occurred to an associated artifact, and if it did,
	 * handles the change.  It basically works by saving the first element it gets and
	 * mapping it to a corresponding element later on (provided that the elements are actually
	 * associated with some alternative).  Two exceptions come up: one, if it's a compilation unit,
	 * we just use the getMovedToElement() method to take care of the mapping for us (we don't have
	 * access to this for single methods due to the confusing/arcane way they decided to code
	 * java element changes).  The other exception is also for compilation units- we have to manually check
	 * the java elements attached to it such as methods and fields to see if any of those are associated
	 * and handle the changes if they are, because we don't get deltas for them.
	 * 
	 * @param change - the possible change, an IJavaElementDelta
	 */
	private void handleChange(IJavaElementDelta change) {
		System.out.println("HandleChange: " + change.getElement() + " " + change.toString() + " " + change.getKind());
		//System.out.println("Saved Delta: " + savedDelta.getElement() + " " + savedDelta.toString() + " " + savedDelta.getKind());
		IJavaElement movedTo = change.getMovedToElement();
		if (change.getKind() == IJavaElementDelta.ADDED || change.getKind() == IJavaElementDelta.REMOVED) {
			if (change.getKind() == IJavaElementDelta.REMOVED && savedDelta == null && movedTo == null) {
				checkDeltaAndSave(change);
			} else if (change.getKind() == IJavaElementDelta.ADDED && savedDelta == null && movedTo == null) {
				// always save this kind of delta, can't check if in DB without reference to removed delta
				savedDelta = change;
				System.out.println("Saved a delta");
			} else {
				IJavaElement oldElt = null;
				IJavaElement newElt = null;
				if (change.getKind() == IJavaElementDelta.ADDED) {
					// get the saved added delta
					oldElt = savedDelta.getElement();
					newElt = change.getElement();
				} else { // change.getKind() == IJavaElementDelta.REMOVED
					if (movedTo != null) {
						// process the move
						checkDeltaAndSave(change); // in this case we're not really saving, just checking
						newElt = movedTo;
						oldElt = change.getElement();
					} else {
						// check the removed delta, then get the saved added delta
						checkDeltaAndSave(change);
						if (savedDelta != null) { // sanity check
							newElt = savedDelta.getElement();
							oldElt = change.getElement();
						}
					}
				}
				// Get the saved association and the old artName
				Association ourAssoc = savedAssoc;
				String oldArtName = oldElt.getElementName();
				// Reset the saved delta and association
				savedDelta = null;
				savedAssoc = null;
				
				// Make sure the association is in the database
				if (ourAssoc != null && ourAssoc.getAlt() != -1) {
					// Find the artifact itself
					IResource newResource = null;
					int cstart = 0;
					IField[] fields = null;
					IMethod[] methods = null;
					try {
						ICompilationUnit compUnit = null;
						if (newElt.getElementType() != IJavaElement.COMPILATION_UNIT) {
							newResource = newElt.getUnderlyingResource();
							compUnit = (ICompilationUnit) newElt.getAncestor(IJavaElement.COMPILATION_UNIT);
						} else {
							newResource = newElt.getCorrespondingResource();
							compUnit = (ICompilationUnit) newElt;
						}
						IType[] myTypes = compUnit.getTypes();
						boolean found = false;
						int i = 0;
						while ((!found) && i < myTypes.length)
						{
							//selected item was the class itself
							if (newElt.getElementType() == IJavaElement.COMPILATION_UNIT)
							{
								if (myTypes[i].isClass())
								{
									found = true;
									cstart = myTypes[i].getNameRange().getOffset();
									fields = myTypes[i].getFields();
									methods = myTypes[i].getMethods();
								}
							}
							else if (newElt.getElementType() == IJavaElement.FIELD)
							{
								IField[] myFields = myTypes[i].getFields();
								for (int j = 0; j< myFields.length; j++)
								{
									if (myFields[j].getElementName().compareTo(newElt.getElementName()) == 0)
									{
										found = true;
										cstart = myFields[j].getNameRange().getOffset();
									}
								}
							}
							else if (newElt.getElementType() == IJavaElement.METHOD)
							{
								IMethod[] myMethods = myTypes[i].getMethods();
								for (int j = 0; j< myMethods.length; j++)
								{
									if (myMethods[j].getElementName().compareTo(newElt.getElementName()) == 0)
									{
										found = true;
										cstart = myMethods[j].getNameRange().getOffset();
									}
								}
							}
							i++;
						} //end while
					} catch (JavaModelException jme) {
						System.err.println(jme.toString());
					}	
					System.out.println("DEBUG: newresource " + newResource.getName() + " cstart " + cstart);
					
					// Get new values for association data
					String newRes = newResource.getName();
					String newArtName = newElt.getElementName();
					String newArt = newElt.getHandleIdentifier();
					
					IResource oldResource = null;
					// Now, the time-consuming part.  If this is a compilation unit, we need to go through
					// all of its fields and methods and if they have associations, update them.
					if (newElt.getElementType() == IJavaElement.COMPILATION_UNIT) {
						for (int i = 0; i < fields.length; i++) {
							checkAssocAndUpdate(fields[i], newRes, null);
						}
						for (int i = 0; i < methods.length; i++) {
							String oldSubArtName = methods[i].getElementName();
							String oldSubArtNameJava = oldSubArtName + ".java";
							if ((oldSubArtName == oldArtName || oldSubArtNameJava == oldArtName) 
									&& oldArtName != newArtName) {
								// Names of old comp unit and old method are same; this is a constructor, and its name has changed
								checkAssocAndUpdate(methods[i], newRes, oldSubArtName);
							} else {
								checkAssocAndUpdate(methods[i], newRes, null);
							}
						}
					}
					// If we're not dealing with a compilation unit, a single method/field was renamed or moved.
					// If it was moved we need to know the old resource to change resource properties (it might not
					// have rationale anymore) so we save that now.
					else {
						oldResource = oldElt.getResource();
					}
					
					// Update them
					ourAssoc.setArtifact(newArt);
					ourAssoc.setResource(newRes);
					ourAssoc.setArtName(newArtName);
						
					System.out.println("oldArt became " + newArt);
					System.out.println("oldRes became " + newRes);
					System.out.println(oldArtName + " became " + newArtName);
							
					// Send the update to the DB
					ourAssoc.toDatabase(oldArtName);
					
					// Update the marker- this will sometimes give us "resource tree locked" exceptions.
					// However, in the cases where it does, we're making changes to a compilation unit
					// and the markers will stay where they are.
					try {
						IMarker ratM = newResource.createMarker("SEURAT.ratmarker");
						String markD = ourAssoc.getMsg();
						ratM.setAttribute(IMarker.MESSAGE, markD);
						ratM.setAttribute(IMarker.CHAR_START, cstart);
						ratM.setAttribute(IMarker.CHAR_END, cstart+1);
						ratM.setAttribute(IMarker.SEVERITY, 0);
						System.out.println(cstart);
						Alternative ourAlt = (Alternative) RationaleDB.getRationaleElement(ourAssoc.getAlt(), RationaleElementType.ALTERNATIVE);
						ratM.setAttribute("alternative", ourAlt.getName());
					} catch (CoreException e) {
						System.err.println(e.toString());
					}
					
					// If the element was moved, remove the persistent property from the old resource.
					// This is a hack, because if there is associated rationale remaining in the old resource
					// it still removes the decorator, but the next time restoreAssociations is called it will
					// be back.  I couldn't see a better way to do it short of rewriting the decorator and/or resource properties manager.
					if (oldResource != null) { // it will be null for a compilation unit because we didn't set it
						SEURATResourcePropertiesManager.addPersistentProperty (oldResource,
								"Rat", "false");
						SEURATDecoratorManager.addSuccessResources (oldResource);
					}
					// add property for the new resource in all cases
					SEURATResourcePropertiesManager.addPersistentProperty (newResource,
							"Rat", "true");
					SEURATDecoratorManager.addSuccessResources (newResource);
				}
			}
		}
	}
	
	/**
	 * Checks whether the artifact a delta refers to is in the DB.  If it is, stores
	 * the delta as savedDelta and the association as savedAssoc.
	 * 
	 * @param delta - IJavaElementDelta that we want to check
	 */
	private void checkDeltaAndSave(IJavaElementDelta delta) {
		Association ourAssoc = new Association();
		String oldArtName = delta.getElement().getElementName();
		ourAssoc.fromDatabase(oldArtName);
		if (ourAssoc.getAlt() != -1) {
			savedDelta = delta;
			savedAssoc = ourAssoc;
			System.out.println("Saved a delta");
		} else {
			System.out.println(oldArtName + " not in DB, did not save delta");
		}
	}
	
	/**
	 * Checks whether an association is in the DB.  If it is, updates the association.
	 * Used for compilation unit children.
	 * 
	 * @param elt - the element that needs to be checked/updated
	 * @param newRes - the new resource
	 * @param oldName - the old name of the element, if it differed from the new name (this only happens
	 * for constructors when the class name is changed), and null if not
	 */
	private void checkAssocAndUpdate(IJavaElement elt, String newRes, String oldName) {
		Association asc = new Association();
		if (oldName != null) {
			// if old name differed from new name, get it from DB using old name
			asc.fromDatabase(oldName);
		} else {
			asc.fromDatabase(elt.getElementName());
		}
		if (asc.getAlt() != -1) {
			// Field was in DB, set variables
			IJavaElement subNewElt = elt;
			String subNewArtName = subNewElt.getElementName();
			String subNewArt = subNewElt.getHandleIdentifier();
			// Update them
			asc.setArtifact(subNewArt);
			asc.setResource(newRes);
			asc.setArtName(subNewArtName);
				
			System.out.println(" (child of comp unit) became " + subNewArt);
					
			// Send the update to the DB
			asc.toDatabase(subNewArtName); // name should be unchanged
		}
	}
}
