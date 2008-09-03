/*
 * Created on Sep 11, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.decorators.*;

/**
 * This class performs the associate in a different direction - it is an
 * option on the alternative and will associate that alternative with
 * whatever code element happens to be selected in the Java Package Explorer.
 * A message is displayed to confirm this, of course.
 * 
 * If there is no java element selected, the user is informed and no action is
 * taken.
 * 
 *  @author jburge
 */
public class AssociateArtifactAction extends Action {
	
	/**
	 * The Rationale viewer
	 */
	private TreeViewer viewer;
	
	/**
	 * The resource selected in the Package Explorer
	 */
	private IJavaElement navigatorSelection;
	
	/**
	 * The rationale element selected
	 */
	private Object obj;
	
	/**
	 * The index for the first character of the artifact we're looking for.  This determines
	 * where we will place the marker once we find it.
	 */
	private int cstart;
	
	/**
	 * The Java resource
	 */
	private IResource ourRes;
	
	/**
	 * Constructor.
	 * @param view - the tree viewer that contains the rationale.
	 */
	public AssociateArtifactAction(TreeViewer view)
	{
		this.viewer = view;
		this.setText("Associate");
		this.setToolTipText("Associate Artifact");
//		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//		getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	/** Executes the association. We get our selected rationale item and 
	 * associated it with the java element selected.
	 * 
	 */
	public void run()
	{
		//Get our selected items. Send a message to the user to confirm that
		//this is the correct association.
		if (navigatorSelection != null)
		{
			ISelection selection = viewer.getSelection();
			obj = ((IStructuredSelection)selection).getFirstElement();
			if (obj instanceof TreeParent)
			{
				String assQ = "Associate '" +
				((TreeParent)obj).getName() + "' with " +
				navigatorSelection.getElementName() + "?";
				
				
				boolean selOk = showQuestion(assQ);
//				showMessage(navigatorSelection.getHandleIdentifier());
//				System.out.println(navigatorSelection.getHandleIdentifier());
				if (selOk)
				{
					cstart = 0;
					
					ourRes = null;
					try {
						
						
						if (navigatorSelection.getElementType() == IJavaElement.COMPILATION_UNIT)
						{
							ourRes = navigatorSelection.getCorrespondingResource(); 
						}
						else
						{
							ourRes = navigatorSelection.getUnderlyingResource();
							if (ourRes != null)
							{
//								***								System.out.println("this one wasn't null?");
							}
							//find the enclosing class file
							IJavaElement nextE = navigatorSelection.getParent();
							while ((nextE != null) && (nextE.getElementType() != IJavaElement.COMPILATION_UNIT))
							{
//								***								System.out.println("Name = " + nextE.getElementName());
//								***							System.out.println("Type = " + nextE.getElementType());
								nextE = nextE.getParent();
							}
							try {
//								***							System.out.println("getting our resource");
								//						ourRes = nextE.getUnderlyingResource();
								ourRes = nextE.getCorrespondingResource();
								ourRes = nextE.getResource();
							} catch (JavaModelException ex)
							{
								System.out.println("exception getting resource?");
							}
//							***							System.out.println("Final name = " + nextE.getElementName());
//							***							System.out.println("Final type = " + nextE.getElementType());
							if (ourRes == null)
							{
								//see if we can get the element from the working copy
								IJavaElement original = nextE.getPrimaryElement();
//								Get working copy has been deprecated
//								IJavaElement original = ((ICompilationUnit) ((ICompilationUnit) nextE).getWorkingCopy()).getOriginalElement();
								ourRes = original.getCorrespondingResource();
								
							}
						}
						//						ourRes = navigatorSelection.getUnderlyingResource();
						if (ourRes == null)
						{
//							***							System.out.println("why would our resource be null?");
						}
//						***						System.out.println("FullPath = " + ourRes.getFullPath().toString());
//						***						System.out.println("now checking file extension?");
						if (ourRes.getFullPath().getFileExtension().compareTo("java") == 0)
						{
//							***							System.out.println("creating our file?");
							IJavaElement myJavaElement = JavaCore.create((IFile) ourRes);						
//							***						System.out.println("created an element?");
							if (myJavaElement.getElementType() == IJavaElement.COMPILATION_UNIT)
							{
//								***						   System.out.println("Compilation Unit");
								ICompilationUnit myCompilationUnit = (ICompilationUnit)
								myJavaElement;
								
								IType[] myTypes = myCompilationUnit.getTypes();
								boolean found = false;
								int i = 0;
								while ((!found) && i < myTypes.length)
								{
									//selected item was the class itself
									if (navigatorSelection.getElementType() == IJavaElement.COMPILATION_UNIT)
									{
//										***						   	 	System.out.println("found the class");
										if (myTypes[i].isClass())
										{
											found = true;
											cstart = myTypes[i].getNameRange().getOffset();
										}
									}
									else if (navigatorSelection.getElementType() == IJavaElement.FIELD)
									{
//										***						   	 	System.out.println("looking for types");
										IField[] myFields = myTypes[i].getFields();
										for (int j = 0; j< myFields.length; j++)
										{
											if (myFields[j].getElementName().compareTo(navigatorSelection.getElementName()) == 0)
											{
//												***									 	System.out.println("found a type");
												found = true;
												cstart = myFields[j].getNameRange().getOffset();
											}
										}
										
									}
									else if (navigatorSelection.getElementType() == IJavaElement.METHOD)
									{
//										***						   	 	System.out.println("looking for a method");
										IMethod[] myMethods = myTypes[i].getMethods();
										for (int j = 0; j< myMethods.length; j++)
										{
											if (myMethods[j].getElementName().compareTo(navigatorSelection.getElementName()) == 0)
											{
//												***									 	System.out.println("found a method");
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
//								***						 	System.out.println("not a compilation unit?");
								System.out.println(myJavaElement.getElementType());
							}
							//ok... now what type is our selected item? 
							System.out.println("got the resource?");
							if (ourRes == null)
							{
								System.out.println("null resource???");
							}
						}
						else
						{
							System.out.println("not a java file?");
						}
//						from the newsgroup - in a runnable?						
						ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
								{
							public void run(IProgressMonitor monitor) {
								try {
//									***						System.out.println("line number = " + new Integer(lineNumber).toString());
									IMarker ratM = ourRes.createMarker("SEURAT.ratmarker");
									String dbname = RationaleDB.getDbName();
									String markD = "Alt: '" +
									((TreeParent)obj).getName() + "'   Rationale DB: '" + dbname + "'";
									ratM.setAttribute(IMarker.MESSAGE, markD);
									ratM.setAttribute(IMarker.CHAR_START, cstart);
									ratM.setAttribute(IMarker.CHAR_END, cstart+1);
									ratM.setAttribute(IMarker.SEVERITY, 0);
									String artName = navigatorSelection.getElementName();
									ratM.setAttribute("alternative", ((TreeParent)obj).getName());
									SEURATResourcePropertiesManager.addPersistentProperty (ourRes,
											"Rat", "true");
									RationaleDB d = RationaleDB.getHandle();
									d.associateAlternative(((TreeParent)obj).getName(),
											navigatorSelection.getHandleIdentifier(),
											ourRes.getName(),
											artName,
											markD);
									
								}
								catch (CoreException e) {
									e.printStackTrace();
								}
							}
								}, null);
//						***						System.out.println("adding persistent property");
						
						SEURATDecoratorManager.addSuccessResources (ourRes);
//						***						System.out.println("added our property");  
						// Refresh the label decorations... Change it to DemoDecoratorWithImageCaching if image caching should be used
//						((TreeParent) obj).setStatus(RationaleErrorLevel.ERROR);
						viewer.update((TreeParent) obj, null);
						SEURATLightWeightDecorator.getRatDecorator().refresh();
//						***						System.out.println("refresh");
						
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						System.out.println("an exception occured in AssociateArtifactAction");
					}
					
				}
				else
					System.out.println("selection rejected");
			}
		}
		else
		{
			System.out.println("No java element selected...");
		}
	}
	
	/**
	 * Show a dialog box to confirm the association
	 * @param message - the message for the user
	 * @return - true if the association is confirmed
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				viewer.getControl().getShell(),
				"RationaleExplorer",
				message);
	}
	
	
	/**
	 * Set the selection when it changes. This is called by the RationaleExplorer
	 * which is a selection listener.
	 * @param sel - our selected Java element from the package explorer
	 */
	public void setSelection(IJavaElement sel)
	{
		navigatorSelection = sel;
	}
}
