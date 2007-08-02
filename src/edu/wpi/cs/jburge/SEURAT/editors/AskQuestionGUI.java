
package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AskQuestionGUI is the dialog box that lets the user type in a reason when
 * they change a field in the rationale.
 * @author burgeje
 *
 */
public class AskQuestionGUI extends Dialog implements Serializable {
	
	
	private static final long serialVersionUID = 3195632975620802133L;
	
	/**
	 * The answer to the question
	 */
	private String answer;
	/**
	 * True if cancelled
	 */
	private boolean cancel;
	
	/**
	 * The answer entered by the user
	 */
	private JTextField descriptionField;
	
	/**
	 * The question
	 */
	private JLabel descLabel;
	private JButton okButton;
	private JButton cancelButton;
	
	
	public AskQuestionGUI(Frame parent, String message)
	{
		
		super(parent, "", true);
		
		answer = new String();
		cancel = false;
		
		this.setLocation(150, 200);
		
		this.setLayout(new BorderLayout(3,3));
		
//		this.add(new JLabel(message), BorderLayout.NORTH);
		
		Box centerBox = Box.createVerticalBox();
		
		JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerBox.add(firstPanel);
		
		descLabel = new JLabel(message + ": ");
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
						answer = descriptionField.getText();
						AskQuestionGUI.this.dispose();
					}
					else
					{
						descLabel.setForeground(Color.red);
					}
					
				}
				else if (e.getActionCommand().equals("cancel"))
				{
					cancel = true;
					AskQuestionGUI.this.dispose();
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
		
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(true);
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(listener);
		buttonPanel.add(cancelButton);
		
		this.pack();
	}
	
	public String getAnswer()
	{
		return answer;
	}
	
	public boolean getCancel()
	{
		return cancel;
	}
	
	
}






