/*
 * Created on Jul 29, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;

import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.decorators.SEURATDecoratorManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;


/**
 *  Removes the associations for a file.
 */
public class RemoveRationaleAssociation extends ActionDelegate implements IViewActionDelegate {
	
	/**
	 * The selected item
	 */
	IStructuredSelection selection;
	
	/**
	 * The resource the selection indicates
	 */
	private IResource ourRes;
	
	/**
	 * Fired when the selected item is changed so a new selection can be stored
	 * @param action - not used
	 * @param selection - the item that has been selected
	 */		
	public void selectionChanged (IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
		
	}
	/**
	 * This is when the action really runs
	 * @param action - not used
	 */		
	public void run (IAction action) 
	{
		IJavaElement navigatorSelection;
//		***		System.out.println("removing association");
		if (selection.getFirstElement() instanceof IJavaElement)
		{
//			***			System.out.println("we are a java element");
			navigatorSelection = (IJavaElement) selection.getFirstElement();
			ourRes = null;
			
			boolean proceed = showQuestion("Do you want to delete all associations to this file?");
			if (!proceed)
			{
				return;
			}
			
			//find the associated resource that goes with the element
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
//						***						System.out.println("this one wasn't null?");
					}
					//find the enclosing class file
					IJavaElement nextE = navigatorSelection.getParent();
					while ((nextE != null) && (nextE.getElementType() != IJavaElement.COMPILATION_UNIT))
					{
//						***						System.out.println("Name = " + nextE.getElementName());
//						***						System.out.println("Type = " + nextE.getElementType());
						nextE = nextE.getParent();
					}
					try {
//						***						System.out.println("getting our resource");
//						ourRes = nextE.getUnderlyingResource();
						ourRes = nextE.getCorrespondingResource();
						ourRes = nextE.getResource();
					} catch (JavaModelException ex)
					{
//						***						System.out.println("exception getting resource?");
					}
					System.out.println("Final name = " + nextE.getElementName());
					System.out.println("Final type = " + nextE.getElementType());
					if (ourRes == null)
					{
//						***						System.out.println("see if there's a working copy");
						IJavaElement original = nextE.getPrimaryElement();
						//see if we can get the element from the working copy
//						IJavaElement original = ((IWorkingCopy) ((ICompilationUnit) nextE).getWorkingCopy()).getOriginalElement();
						ourRes = original.getCorrespondingResource();
					}
				}
				//						ourRes = navigatorSelection.getUnderlyingResource();
				if (ourRes == null)
				{
//					***					System.out.println("why would our resource be null?");
				}
//				***				System.out.println("FullPath = " + ourRes.getFullPath().toString());
//				***				System.out.println("now checking file extension?");
				if (ourRes.getFullPath().getFileExtension().compareTo("java") == 0)
				{
//					***					System.out.println("creating our file?");
					IJavaElement myJavaElement = JavaCore.create((IFile) ourRes);						
//					***	  			    System.out.println("created an element?");
					if (myJavaElement.getElementType() == IJavaElement.COMPILATION_UNIT)
					{
//						***					   System.out.println("Compilation Unit");
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
//								***							System.out.println("found the class");
								if (myTypes[i].isClass())
								{
									found = true;
								}
							}
							else if (navigatorSelection.getElementType() == IJavaElement.FIELD)
							{
//								***							System.out.println("looking for types");
								IField[] myFields = myTypes[i].getFields();
								for (int j = 0; j< myFields.length; j++)
								{
									if (myFields[j].getElementName().compareTo(navigatorSelection.getElementName()) == 0)
									{
//										***									System.out.println("found a type");
										found = true;
									}
								}
								
							}
							else if (navigatorSelection.getElementType() == IJavaElement.METHOD)
							{
//								***							System.out.println("looking for a method");
								IMethod[] myMethods = myTypes[i].getMethods();
								for (int j = 0; j< myMethods.length; j++)
								{
									if (myMethods[j].getElementName().compareTo(navigatorSelection.getElementName()) == 0)
									{
//										***									System.out.println("found a method");
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
//						***						System.out.println("not a compilation unit?");
//						***						System.out.println(myJavaElement.getElementType());
					}
					//ok... now what type is our selected item? 
//					***					System.out.println("got the resource?");
					if (ourRes == null)
					{
//						***						System.out.println("null resource???");
					}
				}
				else
				{
//					***						System.out.println("not a java file?");
				}
//				from the newsgroup - in a runnable?						
				ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
						{
					public void run(IProgressMonitor monitor) {
						try {
//							***									System.out.println("removing our markers, etc.");
//							ourResource.setPersistentProperty()
							SEURATResourcePropertiesManager.addPersistentProperty (ourRes,
									"Rat", "false");
							SEURATDecoratorManager.addSuccessResources (ourRes);
							ourRes.deleteMarkers("SEURAT.ratmarker", true, IResource.DEPTH_INFINITE);
							RationaleDB d = RationaleDB.getHandle();
							d.removeAssociation(ourRes.getName());
						}
						catch (CoreException e) {
							e.printStackTrace();
						}
					}
						}, null);
				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				System.out.println("an exception occured in AssociateArtifactAction");
			}
		}
		
	}
	
	/**
	 * Auto-generated stub
	 */
	public RemoveRationaleAssociation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Auto-generated stub
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Displays a message
	 * @param message - the string defining the message
	 * @return true if the user confirms
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				new Shell(),
				"SEURAT",
				message);
	}
}
