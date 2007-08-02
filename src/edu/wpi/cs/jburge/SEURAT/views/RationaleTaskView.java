/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on June 29, 2003 by lgauthier@opnworks.com
 * 
 * Adapted on October 25, 2003 by Janet Burge for use in SEURAT plugin
 */

package edu.wpi.cs.jburge.SEURAT.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.tasks.*;
/**
 * This is the class that defines our Rationale Task View. This is modeled
 * after the Problems view in Eclipse (which used to be called Tasks in an
 * earlier version...). 
 * 
 * Most of the code involved with this view is in the Tasks package. 
 */

public class RationaleTaskView extends ViewPart  {
	private RationaleTaskListView viewer;
	
	private Action viewRationale;
	private Action override;
	
	/**
	 * The constructor.
	 */
	public RationaleTaskView() {
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new RationaleTaskListView(parent);
		
		makeActions();
		createContextMenu();
		
	}
	
	/**
	 * Create the popup menu for the tasks.
	 *
	 */
	private void createContextMenu() {
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
//				RationaleTaskView.this.fillContextMenu(manager);
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getTableViewer().getControl());
		viewer.getTableViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer.getTableViewer());
	}
	
	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getTableViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		
		if (obj != null)
		{
			
			if (obj instanceof RationaleTask)
			{
				manager.add(viewRationale);
				manager.add(override);
				
			}
			else
			{
//				manager.add(viewRationale);
			}
		}
		manager.add(new Separator("Additions"));
	}
	
	/**
	 * Set up the actions that respond to menu items for our task list. The
	 * two actions supported are viewing (editing) the rationale associated
	 * with the error and overriding the error message.
	 *
	 */
	private void makeActions() {
		//
		// view rationale action
		//
//		System.out.println("making task actions");
		viewRationale = new Action() {
			public void run() {
				
				ISelection selection = viewer.getTableViewer().getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof RationaleTask)
				{ 
					RationaleTask tsk = (RationaleTask) obj;
					RationaleElement ele = 
						RationaleDB.getRationaleElement(tsk.getRationale(), RationaleElementType.fromString(tsk.getRationaleType()));
					boolean canceled = ele.display(viewer.getControl().getDisplay());
					if (!canceled)
					{
						RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
						evt.fireUpdateEvent(ele, viewer.getControl().getDisplay(), UpdateType.UPDATE);
					}	
				}
				
			}
		};
		
		viewRationale.setText("View");
		viewRationale.setToolTipText("View Rationale");
		viewRationale.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		override = new Action() {
			public void run() {
				
				ISelection selection = viewer.getTableViewer().getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof RationaleTask)
				{ 
					viewer.getTaskList().overrideTask((RationaleTask) obj);
				}
				
			}
		};
		
		override.setText("Override");
		override.setToolTipText("Override Error");
		override.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * Handle a 'close' event by disposing of the view
	 */
	
	public void handleDispose() {	
		this.getSite().getPage().hideView(this);
	}
	
}