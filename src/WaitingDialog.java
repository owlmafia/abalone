import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Dialog used when client sends a message and waits synchronously for a response.
 * @author schuetz
 */
public class WaitingDialog extends JDialog {
	
    WaitingDialog(JFrame owner) {
        super(owner, false);
        
        setTitle("Connection");
        
//        JPanel panel = new JPanel();
//        add(panel);
        
        getContentPane().add(new JLabel("Waiting for oponent..."));
        setLocationRelativeTo(owner);
        pack();
    }
}
