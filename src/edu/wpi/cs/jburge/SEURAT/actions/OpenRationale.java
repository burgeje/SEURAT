package edu.wpi.cs.jburge.SEURAT.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.decorators.SEURATDecoratorManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

public class OpenRationale extends ActionDelegate implements
		IObjectActionDelegate {
	
	/**
	 * The selected item
	 */
	IStructuredSelection selection;
	
	/**
	 * The resource the selection indicates
	 */
	private IProject ourProject;
	
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
		IProject ourProject;
//		***		System.out.println("removing association");
		if (selection.getFirstElement() instanceof IProject)
		{
			ourProject = (IProject) selection.getFirstElement();
			System.out.println(ourProject.getName());

		}
		
	}

	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

}
