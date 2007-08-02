
package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ReasonGUI is the dialog box that lets the user type in a reason when
 * they change a field in the rationale.
 * @author burgeje
 *
 */
public class ReasonGUI extends Dialog implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3195632975620802133L;
	
	/**
	 * The reason
	 */
	private String reason;
	/**
	 * The text field where they type in the reason
	 */
	private JTextField descriptionField;
	/**
	 * The label saying this is a reason. This could be a local variable.
	 */
	private JLabel descLabel;
	/**
	 * Button to hit when finished.
	 */
	private JButton okButton;
	
	/**
	 * Constructor for the GUI 
	 * @param parent - points to the parent frame
	 */
	public ReasonGUI(Frame parent)
	{
		
		super(parent, "Enter System", true);
		
		reason = new String();
		
		this.setLocation(150, 200);
		
		this.setLayout(new BorderLayout(3,3));
		
		this.add(new JLabel("Enter Reason for Status Change"), BorderLayout.NORTH);
		
		Box centerBox = Box.createVerticalBox();
		
		JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerBox.add(firstPanel);
		
		descLabel = new JLabel("Reason: ");
		firstPanel.add(descLabel);
		descriptionField = new JTextField( "", 30);
		descriptionField.setHorizontalAlignment(JTextField.LEFT);
		firstPanel.add(descriptionField);
		
		
		
		//inner-class action listener to process button actions
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ok"))
				{
					if (!descriptionField.getText().trim().equals(""))
					{
						reason = descriptionField.getText();
						ReasonGUI.this.dispose();
					}
					else
					{
						descLabel.setForeground(Color.red);
					}
					
				}
			}
			
		};
		
		this.add(centerBox, BorderLayout.CENTER);
		
		
		// Create dialog for the error message
//		errorBox = new MessageBox(parent, "Invalid Login", "Invalid Login");
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		
		okButton = new JButton("Ok");
		okButton.setEnabled(true);
		okButton.setActionCommand("ok");
		okButton.addActionListener(listener);
		buttonPanel.add(okButton);
		
		this.pack();
	}
	
	public String getReason()
	{
		return reason;
	}
	
	
	
}






