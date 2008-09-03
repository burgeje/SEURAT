package edu.wpi.cs.jburge.SEURAT.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;

import SEURAT.editors.RationaleEditorBase;
import SEURAT.editors.RationaleEditorInput;

/**
 * This class was intended to handle the graphical rationale drawing task
 * in a pleasant Eclipse editor tab. However unresolved descrepancies arose,
 * and this class was not able to be completed. It is not currently used.
 * @author Ryan Wagner
 *
 */
public class GraphicalRationale extends RationaleEditorBase {
	private Display display;
	private Composite panel;
	private GC gcBuffer;
	
	public void partClosed(IWorkbenchPart part) {
		gcBuffer.dispose();
	}
	
	public void setupForm(Composite parent){
//		//I can talk to the editor
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 6;
//		gridLayout.marginHeight = 5;
//		gridLayout.makeColumnsEqualWidth = true;
//		parent.setLayout(gridLayout);
//		//new Label(parent, SWT.NONE).setText("Sanity Check");
	    
		//but this Canvas is nonfunctional and panel.getSize() returns (0,0)

//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.horizontalSpan = 6;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalSpan = 5;
//		gridData.grabExcessVerticalSpace = true;
	    //panel.setLayoutData(gridData);
		
		display = parent.getDisplay();
	    panel = new Canvas(parent,SWT.BORDER);
	    //panel.setSize(500,500);
	    Point panelSize = panel.getSize();

	    Image image = new Image(display,SEURATPlugin.getImageDescriptor("newReq.gif").getImageData());
	    
	    Image buffer = new Image(display,500,500);
	    GC gcBuffer = new GC(panel);    
	    gcBuffer.setBackground(display.getSystemColor(SWT.COLOR_WHITE));  
	    gcBuffer.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    
	    gcBuffer.fillRectangle(0,0,500,500);
	    gcBuffer.drawLine(0,500,500,0);
	    
	    
	    gcBuffer.dispose();
	    GC gcPanel = new GC(panel);
	    gcPanel.drawImage(buffer,0,0);
	    gcPanel.dispose();
	    buffer.dispose();   
	    
	    //another attempt
//	    Image image = new Image(display,SEURATPlugin.getImageDescriptor("newReq.gif").getImageData());
//	    GC gc = new GC(image);
//	    Rectangle bounds = image.getBounds();
//		gc.drawLine(0,0,bounds.width,bounds.height);
//		gc.drawLine(0,bounds.height,bounds.width,0);
//		gc.dispose();
//	    image.dispose(); 
//	    
	    
	}
	//we are masquerading as a rationale editor in order to use boiler plate code for the editors
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new GraphicalRationale.Input(explorer, tree, parent, target, new1);
	}
	public Class editorType(){
		return GraphicalRationale.class;
	}
	public RationaleElement getRationaleElement() {
		//we don't have a rationale element, we're a view; this could break things
		return null;
	}
	public boolean saveData(){
		//we don't save anything, we're a view
		return true;
	}
	@Override
	public boolean isDirty(){ //we can never be dirty
		return false;
	}
	
	public static class Input extends RationaleEditorInput {
		
		public Input(RationaleExplorer explorer, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}
		
		@Override
		public String getName() {
			return "Graphical Rationale";
		}

		@Override
		public boolean targetType(Class type) {
			return type == GraphicalRationale.class;
		}
		//this prevents a nasty null pointer, but doesn't stop the user from opening multiple Graphical Rationale tabs
		//TODO: prevent multiple Graphical Rationale editors, figure out why the NPE is thrown, remove this hack
		@Override
		public boolean equals(Object obj){
			return false;
		}
	}


}
