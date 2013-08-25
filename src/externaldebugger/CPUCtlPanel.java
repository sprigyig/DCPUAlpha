package externaldebugger;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import debug.DebugCommand;
import debug.DebugCommandType;
import debug.DebugResponse;
import externaldebugger.CPURemote.ResponseExpectation;
import externaldebugger.CPUStatePanel.UpdateWatcher;


public class CPUCtlPanel extends JPanel implements UpdateWatcher, ResponseExpectation {
	private static final long serialVersionUID = 5559878460299891799L;
	private MemWatcherPanel sp;
	private MemWatcherPanel pc;
	private CPUStatePanel state;
	private long cpuid;
	private CPURemote remote; 
	
	public CPUCtlPanel(CPURemote premote, long pcpuid, CPUStatePanel state, MemWatcherPanel pc, MemWatcherPanel sp) {
		super(new GridLayout(2,2));
		this.remote = premote;
		this.cpuid = pcpuid;
		this.state = state;
		this.pc = pc;
		this.sp = sp;
		
		state.watcher = this;
		remote.setUnexpectedHandler(this);
		JPanel buttonPan = new JPanel(new GridLayout(0,1));
		JButton refresh;
		buttonPan.add(refresh = new JButton("refresh"));
		JButton pause;
		buttonPan.add(pause = new JButton("pause"));
		JButton resume;
		buttonPan.add(resume = new JButton("resume"));
		JButton step;
		buttonPan.add(step = new JButton("step"));
		buttonPan.add(new JLabel(""));
		buttonPan.add(new JLabel("Breakpoint Toggle:"));
		final JTextField bpfield;buttonPan.add(bpfield = new JTextField());
		final JButton bpbutton;
		buttonPan.add(bpbutton = new JButton("Toggle Break on 0000"));
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DebugCommand cmd = new DebugCommand();
				cmd.type = DebugCommandType.PAUSE;
				cmd.params = new long[]{cpuid};
				remote.commandResponse(cmd);
				refresh();
			}
		});
		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DebugCommand cmd = new DebugCommand();
				cmd.type = DebugCommandType.UNPAUSE;
				cmd.params = new long[]{cpuid};
				remote.commandResponse(cmd);
			}
		});
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DebugCommand cmd = new DebugCommand();
				cmd.type = DebugCommandType.STEP;
				cmd.params = new long[]{cpuid};
				remote.commandResponse(cmd);
			}
		});
		bpfield.getDocument().addDocumentListener(new DocumentListener() {
			void changed() {
				String s = bpfield.getText();
				try {
					Integer.parseInt(s, 16);
					if (s.length()<=4) {
						bpbutton.setText("(Un)break:"+bpfield.getText());
						bpbutton.setEnabled(true);
						return;
					}
				} catch(NumberFormatException e) {
					
				}
				bpbutton.setText("Bad Number");
				bpbutton.setEnabled(false);
			}
			
			public void removeUpdate(DocumentEvent arg0) {
				changed();
			}
			
			public void insertUpdate(DocumentEvent arg0) {
				changed();
			}
			
			public void changedUpdate(DocumentEvent arg0) {
				changed();
			}
		});
		bpbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DebugCommand cmd = new DebugCommand();
				cmd.type = DebugCommandType.BREAK;
				cmd.params = new long[]{cpuid, Integer.parseInt(bpfield.getText(),16)};
				remote.commandResponse(cmd);
			}
		});
		
		bpfield.setText("0000");
		
		while(buttonPan.getComponents().length<8) {
			buttonPan.add(new JLabel(""));
		}
		
		add(buttonPan);
		add(state);
		add(pc);
		add(sp);
	}

	public void registersUpdated(long[] lvalues) {
		sp.setBase((int) lvalues[11]-8);
		sp.setHighlight((int) lvalues[11]);
		
		pc.setBase((int) lvalues[10]-8);
		pc.setHighlight((int) lvalues[10]);
		
		sp.update();
		pc.update();
	}
	
	private void refresh() {
		state.update();
	}

	public void response(DebugResponse r) {
		state.update();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {

	
	}

	public static void open(String text, int port, int cpu) throws UnknownHostException, IOException {
		JFrame jf = new JFrame();
		CPURemote r = new CPURemote(cpu, new Socket("localhost", port));
		CPUStatePanel state = new CPUStatePanel(r, 0);
		MemWatcherPanel pc = new MemWatcherPanel(r, 0, 0, 16);
		MemWatcherPanel sp = new MemWatcherPanel(r, 0, 0, 16);
		final CPUCtlPanel pan = new CPUCtlPanel(r, 0, state, pc, sp); 
		
		jf.add(pan);
		
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
	}
}
