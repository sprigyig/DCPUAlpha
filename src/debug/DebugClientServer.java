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

	public DebugClientServer(Socket sock, Collection<Dcpu> cpus, DebugServer debugServer) {
		this.sock = sock;
		this.cpus = cpus;
		this.server = debugServer;
		new Thread(this).start();
	}

	public void run() {
		try {
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			Scanner s = new Scanner(sock.getInputStream());
			while(!server.die) {
				String json = s.nextLine();
				System.out.println("GOT JSON:"+json);
				Gson g = new Gson();
				try {
					DebugCommand cmd = g.fromJson(json, DebugCommand.class);
					System.out.println("CMD:"+cmd);
					Object o = cmd.type.action.run(cpus, cmd.params);
					System.out.println("action run");
					pw.println(g.toJson(o));
					pw.flush();
				} catch(Exception e) {
					pw.println(g.toJson(new DebugResponse(CommandStatus.NOT_UNDERSTOOD,"wat",new long[]{})));
				}
			}
			s.close();
		} catch (IOException e) {
		}
		
	}
}