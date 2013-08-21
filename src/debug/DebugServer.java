package debug;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import dcpu.Dcpu;

public class DebugServer implements Runnable {
	boolean die;
	private ServerSocket server;
	private Collection<Dcpu> cpus;
	
	
	public DebugServer(Collection<Dcpu>cpus) {
		die = false;
		this.cpus = cpus;
		new Thread(this).start();
	}

	public void run() {
		int failures = 0;
		int port = 40300;
		while (failures < 100) {
			try {
				server = new ServerSocket(port+failures);
				break;
			} catch (IOException e) {
				server = null;
				failures +=1;
				System.out.println("failed");
			}
		}
		if (server == null) {
			System.err.println("Could not find open socket");
			return;
		} else {
			System.out.println("Debug Server runnning on port "+(port+failures));
		}
		
		try {
			server.setSoTimeout(5000);
		} catch (SocketException e) {}		
		
		while(!die) {
			try {
				new DebugClientServer(server.accept(), cpus, this);
			} catch (IOException e) {
			}
		}
	}
	
	public static void main(String[] args) {
		new DebugServer(new ArrayList<Dcpu>());
	}
}
