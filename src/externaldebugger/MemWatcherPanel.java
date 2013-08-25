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


public class MemWatcherPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private CPURemote remote;
	private long cpu;
	
	private JLabel[] addrs;
	private JLabel[] contents;
	
	int base;
	private int highlight;

	public MemWatcherPanel (CPURemote r, long cpu, int base,  int height) {
		super(new GridLayout(0,2));
		this.remote = r;
		this.cpu = cpu;
		
		addrs = new JLabel[height];
		contents = new JLabel[height];
		
		for (int i=0; i<height; i++) {
			addrs[i] = new JLabel("0000");
			contents[i] = new JLabel("0000");
			add(addrs[i]);
			add(contents[i]);
		}
		this.base = base;
		update();
	}

	public void update() {
		new Thread() {
			public void run() {
				DebugCommand cmd = new DebugCommand();
				cmd.type = DebugCommandType.LISTMEM;
				cmd.params = new long[]{cpu, base, addrs.length};
				DebugResponse resp = remote.commandResponse(cmd);
				System.out.println(resp.userAlert);
				for (int i=0; i<resp.payload.length; i++) {
					addrs[i].setText(String.format((base+i==highlight ? "*" : "")+"%04x", base+i));
					contents[i].setText(String.format("%04x", resp.payload[i]));
				}
			}
		}.start();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		JFrame jf = new JFrame();
		CPURemote r = new CPURemote(0, new Socket("localhost", 40300));
		final MemWatcherPanel pan;
		jf.add(pan = new MemWatcherPanel(r, 0, 0x50, 10));
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				pan.update();
			}
		});
		pan.setHighlight(0x58);
	}
	
	public void setBase(int base) {
		if (base < 0) base = 0;
		
		if (base+addrs.length > 0x10000) {
			base = 0x10000 - addrs.length;
		}
		this.base = base;
	}
	
	public void setHighlight(int highlight) {
		this.highlight = highlight;
	}
}
