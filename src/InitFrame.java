import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class InitFrame extends JFrame {

	private Server server;
	private ConnectionDialog connectionDialog;
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		InitFrame connectionFrame = new InitFrame();
	}
	
	InitFrame() {
		setTitle("Abalone");
		
		JLabel imageLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("img/abalone1.jpg")));
		
		JPanel connectionPanel = new JPanel();
		BoxLayout nextPanelLayout = new BoxLayout(connectionPanel, javax.swing.BoxLayout.Y_AXIS);
		connectionPanel.setLayout(nextPanelLayout);
		
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new FlowLayout());
		colorPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		JLabel colorTextLabel = new JLabel("Color: ");
		
		final ImageIcon whiteImage = new ImageIcon(getClass().getClassLoader().getResource("img/whitemarble1.png"));
		final ImageIcon blackImage = new ImageIcon(getClass().getClassLoader().getResource("img/blackmarble1.png"));
		final ImageIcon whiteSelectedImage = new ImageIcon(getClass().getClassLoader().getResource("img/whitemarble3.png"));
		final ImageIcon blackSelectedImage = new ImageIcon(getClass().getClassLoader().getResource("img/blackmarble3.png"));
		
		final JLabel whiteImageLabel = new JLabel(whiteImage);
		final JLabel blackImageLabel = new JLabel(blackImage);

		final Player[] selectedPlayer = new Player[1];
		
		whiteImageLabel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent arg0) {
				blackImageLabel.setIcon(blackImage);
				whiteImageLabel.setIcon(whiteSelectedImage);
				selectedPlayer[0] = Player.white;
			}
		});
		blackImageLabel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent arg0) {
				whiteImageLabel.setIcon(whiteImage);
				blackImageLabel.setIcon(blackSelectedImage);
				selectedPlayer[0] = Player.black;
			}
		});
		
		JPanel buttonsPanel = new JPanel();
		JButton serverButton = new JButton("Start as server");
		serverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				server = new Server();
				server.start();
				Client client = new Client("localhost");
				ClientInitializator clientInitializator = new ClientInitializator(InitFrame.this, client, selectedPlayer[0]);
				new Thread(clientInitializator).start();
				connectionDialog = new ConnectionDialog(InitFrame.this);
				connectionDialog.setVisible(true);
			}
		});
		JButton clientButton = new JButton("Start as client");
		clientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String serverIp = JOptionPane.showInputDialog(InitFrame.this, "Enter server's ip:", "Connect with server", JOptionPane.PLAIN_MESSAGE);
				Client client = new Client(serverIp);
				setVisible(false);
				Player player = client.getPlayer(selectedPlayer[0]);
				@SuppressWarnings("unused")
				Game game = new Game(client, player);
			}
		});
		
		colorPanel.add(colorTextLabel);
		colorPanel.add(whiteImageLabel);
		colorPanel.add(blackImageLabel);
		
		buttonsPanel.add(serverButton);
		buttonsPanel.add(clientButton);

		connectionPanel.add(colorPanel);
		connectionPanel.add(buttonsPanel);
		
		add(imageLabel, BorderLayout.NORTH);
		add(connectionPanel, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				if (!(server == null)) {
					shutDownServer();
				}
				System.exit(0);
		  }
		});
		pack();
		setVisible(true);
	}
	
	void hideComponents() {
		setVisible(false);
		connectionDialog.setVisible(false);
	}
	
	void shutDownServer() {
		server.shutDown();
	}
}

class ConnectionDialog extends JDialog {
	
    private JButton cancelButton;
    private InitFrame initFrame;
    
    public ConnectionDialog(InitFrame initFrame) {
        super(initFrame, true);
        this.initFrame = initFrame;
        
        setTitle("Connection");
        
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        
        panel.add(new JLabel("Waiting for oponent..."));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 ConnectionDialog.this.initFrame.shutDownServer();
        	     setVisible(false);
        	 }
        });
        panel.add(cancelButton); 
        pack();
        setLocationRelativeTo(initFrame);
    }
}

class ClientInitializator implements Runnable {
	
	private InitFrame initFrame;
	private Client client;
	private Player selectedPlayer;
	
	ClientInitializator(InitFrame initFrame, Client client, Player selectedPlayer) {
		this.initFrame = initFrame;
		this.client = client;
		this.selectedPlayer = selectedPlayer;
	}
	
	public void run() {
		Player player = client.getPlayer(selectedPlayer);
		initFrame.hideComponents();
		@SuppressWarnings("unused")
		Game game = new Game(client, player);
		System.out.println("clientinitializator end");
	}
}