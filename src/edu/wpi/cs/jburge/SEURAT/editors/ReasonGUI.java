
package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * ReasonGUI is the dialog box that lets the user type in a reason when
 * they change a field in the rationale.
 * Changed by Team 1: eliminated AWT dialogs (caused Eclipse to crash on Mac OS).
 * @author burgeje
 *
 */
public class ReasonGUI implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3195632975620802133L;
	
	/**
	 * The reason
	 */
	private String reason;
	
	/**
	 * Constructor for the GUI
	 */
	public ReasonGUI()
	{
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
				"Rationale Status Changed", "Enter Reason for Status Change:",
				null, null);

		if (dlg.open() == Window.OK) {
			reason = dlg.getValue();

		}
	}
	
	/**
	 * Get the reason
	 * @return reason for change that the user entered
	 */
	public String getReason()
	{
		return reason;
	}	
}
