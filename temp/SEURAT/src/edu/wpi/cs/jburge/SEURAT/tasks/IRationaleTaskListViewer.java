package edu.wpi.cs.jburge.SEURAT.tasks;

/*
 * This is the interface into our RationaleTaskListViewer
 * 
 * This code is based on the Eclipse Corner article 
 * "Building and delivering a table editor with SWT/JFace"
 * http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 * and on the example attached to the article:
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 *
 */

public interface IRationaleTaskListViewer {
	
	/**
	 * Update the view when a task is added to the task list
	 * @param task - the task being added
	 */
	public void addTask(RationaleTask task);
	
	/**
	 * Update the view when a task is removed
	 * @param task - the task being removed
	 */
	public void removeTask(RationaleTask task);
	
	/**
	 * Update the view when a task is modified
	 * @param task - the modified task
	 */
	public void updateTask(RationaleTask task);
}
