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
 * @author jburge
 *
 * This class restores associations from the database. This functionality was added to assist with the SEURAT 
 * evaluation so that SEURAT could be "reset" between subjects. The problem with restoring associations and
 * bookmarks in this way is that it uses the line numbers from when the associations were created which will
 * be incorrect if the code has been modified. Still, the code is being left in place because we may want to do
 * something similar when converting SEURAT into a multi-user system. Bookmarks and other markers are only stored
 * locally and can not be shared between users unless they are saved and restored from a database or other
 * shared data repository.
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
	 * Constructor
	 * @param view - our tree viewer
	 */
	public RestoreAssociations(TreeViewer view)
	{
		this.setText("Restore");
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
		Vector ourResources = d.getAssociations();
		Iterator resI = ourResources.iterator();
		try
		{
			
			while (resI.hasNext())
			{
				ourAssoc = (Association) resI.next();
				String ourResName = ourAssoc.getResource();
				try{
					
					IJavaElement ourEle = JavaCore.create(ourResName);
					
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
//								ratM.setAttribute(IMarker.CHAR_START, 153);
//								ratM.setAttribute(IMarker.CHAR_END, 154);
								ratM.setAttribute(IMarker.SEVERITY, 0);
								ratM.setAttribute(IMarker.LINE_NUMBER, ourAssoc.getLineNumber());
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
					System.out.println("couldn't create our element " + ourResName);
				}
				
			}
		} catch (Exception ex)
		{
			System.out.println("exception while trying to add associations");
		}
		
	}
	
}
