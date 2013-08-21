package debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Scanner;

import com.google.gson.Gson;

import dcpu.Dcpu;

class DebugClientServer implements Runnable {
	Socket sock;
	private Collection<Dcpu> cpus;
	private DebugServer server;
	private PrintWriter pw;
	private Scanner s;

	public DebugClientServer(Socket sock, Collection<Dcpu> cpus, DebugServer debugServer) {
		this.sock = sock;
		this.cpus = cpus;
		this.server = debugServer;
		new Thread(this).start();
	}
	
	void sendResponse(DebugResponse d) {
		Gson g = new Gson();
		pw.println(g.toJson(d));
		pw.flush();
	}

	public void run() {
		try {
			this.pw = new PrintWriter(sock.getOutputStream());
			this.s = new Scanner(sock.getInputStream());
			while(!server.die) {
				String json = s.nextLine();
				Gson g = new Gson();
				try {
					DebugCommand cmd = g.fromJson(json, DebugCommand.class);
					sendResponse(cmd.type.action.run(cpus, cmd.params, this));
					
				} catch(Exception e) {
					e.printStackTrace();
					sendResponse(new DebugResponse(CommandStatus.NOT_UNDERSTOOD,"wat",new long[]{}));
				}
			}
			s.close();
		} catch (IOException e) {
		}
		
	}
}