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

package SEURAT.editors.unused;

import SEURAT.editors.*;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.ReasonGUI;
import edu.wpi.cs.jburge.SEURAT.rationaleData.History;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Assumption;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class AssumptionEditor extends RationaleEditorBase {
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new AssumptionEditor.Input(explorer, tree, parent, target, new1);
	}		
	
	private Text nameField;
	private Text descArea;
	private Button enableButton;
	
	public Class editorType() {
		return Assumption.class;
	}
	
	public RationaleElement getRationaleElement() {
		return getAssumption();
	}

	public Assumption getAssumption() {
		return (Assumption)getEditorData().getAdapter(Assumption.class);
	}
	
	public void setupForm(Composite parent) {		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getAssumption().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		DisplayUtilities.setTextDimensions(nameField, gridData, 150);
		nameField.setLayoutData(gridData);
		nameField.addModifyListener(getNeedsSaveListener());
		
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getAssumption().getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData,100, 2);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);
		descArea.addModifyListener(getNeedsSaveListener());
				
		enableButton = new Button(parent, SWT.CHECK);
		enableButton.setText("Enabled");
		enableButton.setSelection(getAssumption().getEnabled());
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		enableButton.setLayoutData(gridData);
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
	}
	
	
	public boolean saveData() {
		ConsistencyChecker checker = new ConsistencyChecker(getAssumption().getID(), nameField.getText(), "Assumptions");
		
		if(!nameField.getText().trim().equals("") &&
				(getAssumption().getName() == nameField.getText() || checker.check()))
		{
			//TODO needs a setParent method
			getAssumption().setName(nameField.getText());
			getAssumption().setDescription(descArea.getText());
			getAssumption().setEnabled(enableButton.getSelection());

			//since this is a save, not an add, the type and parent are ignored
			getAssumption().toDatabase();
			return true;
		}
		else
		{
			String l_message = "";
			l_message += "The assumption name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " an assumption name and the assumption name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Assumption Name Is Invalid");
			mbox.open();
		}
		return false;
	}
	
	public static class Input extends RationaleEditorInput {
		
		public Input(RationaleExplorer explorer, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}

		public Assumption getData() { return (Assumption)getAdapter(Assumption.class); }
		
		@Override
		public String getName() {
			return isCreating() ? "New Assumption Editor" :
				"Assumption: " + getData().getName();
		}

		@Override
		public boolean targetType(Class type) {
			return type == Assumption.class;
		}		
	}
}

