import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class CancelableWaitingDialog extends JDialog {

	public CancelableWaitingDialog(JFrame owner, final CancelListener cancelListener) {
		super(owner, false);
		getContentPane().add(new JLabel("Waiting for oponent..."));
		JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 cancelListener.processCancel();
        	     setVisible(false);
        	 }
        });
        getContentPane().add(cancelButton); 
	}
}
