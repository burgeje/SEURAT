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
 * Created on Sep 11, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.actions;


import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.*;
import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.decorators.*;

/**
 * This class restores associations from the database. This functionality was added to assist with the SEURAT 
 * evaluation so that SEURAT could be "reset" between subjects, because bookmarks and other markers are only
 * stored locally and cannot be shared between users unless they are saved and restored from a database or 
 * other shared data repository.  Originally, the class used the line numbers from when the associations
 * were created to restore the associations.  This has been improved and it no longer depends on the line
 * numbers.  However, if the code has been refactored the associations will not be restored properly, and
 * this problem will need to be solved or avoided if SEURAT is to function properly in a multi-user environment.
 */
public class RestoreAssociations extends Action {
	//needed because of our inner class experiment
//	private Object obj;
	/**
	 * The association being restored
	 */
	private Association ourAssoc;
	/**
	 * The resource associated with
	 */
	private IResource ourRes;
	
	/**
	 * The index for the first character of the artifact we're looking for.  This determines
	 * where we will place the marker once we find it.
	 */
	private int cstart;
	
	/**
	 * Constructor
	 * @param view - our tree viewer
	 */
	public RestoreAssociations(TreeViewer view)
	{
		this.setText("Restore Associations");
		this.setToolTipText("Restore Associations");
//		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//		getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	/**
	 * This is where the action takes place when the restore is invoked from the SEURAT pull-down menu.
	 */
	public void run()
	{
		//find all our resources
		RationaleDB d = RationaleDB.getHandle();
		Vector<Association> ourResources = d.getAssociations();
		Iterator<Association> resI = ourResources.iterator();
		try
		{
			
			while (resI.hasNext())
			{
				cstart = 0;
				ourAssoc = (Association) resI.next();
				String ourArtifact = ourAssoc.getArtifact();

				//System.out.println(ourArtifact);
				//System.out.println(ourAssoc.getResource());
				try{
					// We create the java element from its artifact that is stored in the DB
					// and then search through the resource to find out where the marker needs to be placed.
					IJavaElement ourEle = JavaCore.create(ourArtifact);
					//System.out.println(ourEle.getElementName() + " " + ourEle.getElementType());
					
					if (ourEle.getElementType() == IJavaElement.COMPILATION_UNIT)
					{
						ourRes = ourEle.getCorrespondingResource(); 
					}
					else
					{
						ourRes = ourEle.getUnderlyingResource();
						if (ourRes != null)
						{
//							***								System.out.println("this one wasn't null?");
						}
						//find the enclosing class file
						IJavaElement nextE = ourEle.getParent();
						while ((nextE != null) && (nextE.getElementType() != IJavaElement.COMPILATION_UNIT))
						{
//							***								System.out.println("Name = " + nextE.getElementName());
//							***							System.out.println("Type = " + nextE.getElementType());
							nextE = nextE.getParent();
						}
						try {
//							***							System.out.println("getting our resource");
							//						ourRes = nextE.getUnderlyingResource();
							ourRes = nextE.getCorrespondingResource();
							ourRes = nextE.getResource();
						} catch (JavaModelException ex)
						{
							System.out.println("exception getting resource?");
						}
//						***							System.out.println("Final name = " + nextE.getElementName());
//						***							System.out.println("Final type = " + nextE.getElementType());
						if (ourRes == null)
						{
//							***								System.out.println("see if there's a working copy");
							IJavaElement original = nextE.getPrimaryElement();
							//see if we can get the element from the working copy
//							IJavaElement original = ((IWorkingCopy) ((ICompilationUnit) nextE).getWorkingCopy()).getOriginalElement();
							ourRes = original.getCorrespondingResource();
						}
					}
					//						ourRes = navigatorSelection.getUnderlyingResource();
					if (ourRes == null)
					{
//						***							System.out.println("why would our resource be null?");
					}
//					***						System.out.println("FullPath = " + ourRes.getFullPath().toString());
//					***						System.out.println("now checking file extension?");
					if (ourRes.getFullPath().getFileExtension().compareTo("java") == 0)
					{
//						***							System.out.println("creating our file?");
						IJavaElement myJavaElement = JavaCore.create((IFile) ourRes);						
//						***						System.out.println("created an element?");
						if (myJavaElement.getElementType() == IJavaElement.COMPILATION_UNIT)
						{
//							***						   System.out.println("Compilation Unit");
							ICompilationUnit myCompilationUnit = (ICompilationUnit) myJavaElement;
							
							IType[] myTypes = myCompilationUnit.getTypes();
							boolean found = false;
							int i = 0;
							while ((!found) && i < myTypes.length)
							{
								//selected item was the class itself
								if (ourEle.getElementType() == IJavaElement.COMPILATION_UNIT)
								{
//									***						   	 	System.out.println("found the class");
									if (myTypes[i].isClass())
									{
										found = true;
										cstart = myTypes[i].getNameRange().getOffset();
									}
								}
								else if (ourEle.getElementType() == IJavaElement.FIELD)
								{
//									***						   	 	System.out.println("looking for types");
									IField[] myFields = myTypes[i].getFields();
									for (int j = 0; j< myFields.length; j++)
									{
										if (myFields[j].getElementName().compareTo(ourEle.getElementName()) == 0)
										{
//											***									 	System.out.println("found a type");
											found = true;
											cstart = myFields[j].getNameRange().getOffset();
										}
									}
									
								}
								else if (ourEle.getElementType() == IJavaElement.METHOD)
								{
//									***						   	 	System.out.println("looking for a method");
									IMethod[] myMethods = myTypes[i].getMethods();
									for (int j = 0; j< myMethods.length; j++)
									{
										if (myMethods[j].getElementName().compareTo(ourEle.getElementName()) == 0)
										{
//											***							 	System.out.println("found a method");
											found = true;
											cstart = myMethods[j].getNameRange().getOffset();
										}
									}
								}
								//don't forget to increment!
								i++;
							} //end while
							
						}
						else
						{
//							***						 	System.out.println("not a compilation unit?");
							System.out.println(myJavaElement.getElementType());
						}
						//ok... now what type is our selected item? 
						System.out.println("got the resource?");
						if (ourRes == null)
						{
//							***							System.out.println("null resource???");
						}
					}
					else
					{
//						***							System.out.println("not a java file?");
					}
//					from the newsgroup - in a runnable?						
					ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
							{
						public void run(IProgressMonitor monitor) {
							try {
//								***						System.out.println("line number = " + new Integer(lineNumber).toString());
								IMarker ratM = ourRes.createMarker("SEURAT.ratmarker");
								String markD = ourAssoc.getMsg();
								ratM.setAttribute(IMarker.MESSAGE, markD);
								ratM.setAttribute(IMarker.CHAR_START, cstart);
								ratM.setAttribute(IMarker.CHAR_END, cstart+1);
								ratM.setAttribute(IMarker.SEVERITY, 0);
								System.out.println(cstart);
								Alternative ourAlt = (Alternative) RationaleDB.getRationaleElement(ourAssoc.getAlt(), RationaleElementType.ALTERNATIVE);
								ratM.setAttribute("alternative", ourAlt.getName());
								SEURATResourcePropertiesManager.addPersistentProperty (ourRes,
										"Rat", "true");
								
							}
							catch (CoreException e) {
								e.printStackTrace();
							}
						}
							}, null);
//					***						System.out.println("adding persistent property");
					
					SEURATDecoratorManager.addSuccessResources (ourRes);
//					***						System.out.println("added our property");  
					// Refresh the label decorations... Change it to DemoDecoratorWithImageCaching if image caching should be used
//					((TreeParent) obj).setStatus(RationaleErrorLevel.ERROR);
					//				  viewer.update((TreeParent) obj, null);
					//for some reason the next line is giving us an exception - ???
					//it doesn't seem to be needed - the decorator appears.
					//			SEURATLightWeightDecorator.getRatDecorator().refresh();
//					***						System.out.println("refresh");
					
				}
				catch (Exception e)
				{
					System.out.println("couldn't create our element " + ourArtifact);
				}
				
			}
		} catch (Exception ex)
		{
			System.out.println("exception while trying to add associations");
		}
		
	}
	
}
