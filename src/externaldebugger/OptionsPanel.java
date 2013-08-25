package externaldebugger;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class OptionsPanel extends JPanel {
	private static final long serialVersionUID = -3501982254481009632L;
	private JTextField port;
	private JTextField cpu;
	private JButton connect;
	private JTextField hostname;

	public OptionsPanel() {
		super(new GridLayout(0,2));
		
		this.hostname = new JTextField("localhost");
		this.port = new JTextField("40300");
		this.cpu = new JTextField("0");
		this.connect = new JButton("connect");
		add(new JLabel("hostname"));
		add(hostname);
		add(new JLabel("port"));
		add(port);
		add(new JLabel("cpu #"));
		add(cpu);
		add(connect);
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CPUCtlPanel.open(hostname.getText(), Integer.parseInt(port.getText()), Integer.parseInt(cpu.getText()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.add(new OptionsPanel());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}
}
