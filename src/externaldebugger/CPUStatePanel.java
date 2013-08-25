package externaldebugger;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import debug.DebugCommand;
import debug.DebugCommandType;
import debug.DebugResponse;


public class CPUStatePanel extends JPanel {
	private static final long serialVersionUID = 8706770369703977217L;
	private CPURemote remote;
	private long cpu;
	
	private JLabel values[];
	private long[] lvalues;
	
	public static interface UpdateWatcher {
		public void registersUpdated(long[] lvalues);
	}
	
	public UpdateWatcher watcher;
	
	public CPUStatePanel(CPURemote r, long cpu) {
		super(new GridLayout(0,2));
		this.remote = r;
		this.cpu = cpu;
		
		String[] names = new String[]{"A","B","C","X","Y","Z","I","J","EX","IA","PC","SP"};
		values = new JLabel[names.length];
		lvalues = new long[names.length];
		
		for (int i=0; i<names.length; i++) {
			add(new JLabel(names[i]));
			values[i] = new JLabel("");
			add(values[i]);
		}
		
		
		update();
	}

	public void update() {
		new Thread() {
			public void run() {
				DebugCommand cmd = new DebugCommand();
				cmd.params = new long[]{cpu};
				cmd.type = DebugCommandType.CPUREGS;
				DebugResponse resp = remote.commandResponse(cmd);
				for (int i=0; i<resp.payload.length; i++) {
					values[i].setText(String.format("%04x", resp.payload[i]));
					lvalues[i] = resp.payload[i];
				}
				
				if (watcher != null) watcher.registersUpdated(lvalues);
			}
		}.start();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		JFrame jf = new JFrame();
		CPURemote r = new CPURemote(0, new Socket("localhost", 40300));
		final CPUStatePanel pan;
		jf.add(pan = new CPUStatePanel(r, 0));
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				pan.update();
			}
		});
	}
}
