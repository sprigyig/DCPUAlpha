package externaldebugger;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import debug.CommandStatus;
import debug.DebugCommand;
import debug.DebugResponse;


public class CPURemote {
	int targetCpu;
	
	Scanner input;
	PrintWriter output;
	
	private Object expectLock;
	private ResponseExpectation expect;
	
	private ResponseExpectation unexpectedHandler;
	
	public static interface ResponseExpectation {
		public void response(DebugResponse r);
	}
	
	public CPURemote(int targetCpu, Socket remote) throws IOException {
		super();
		this.targetCpu = targetCpu;
		input = new Scanner(remote.getInputStream());
		output = new PrintWriter(remote.getOutputStream());
		expectLock = new Object();
		expect = null;
		new Thread(new Runnable() {
			public void run() {
				readproc();
			}
		}).start();
	}
	
	private void readproc() {
		while(true) {
			String json = input.nextLine();
			System.out.println("json:" +json);
			DebugResponse response = new Gson().fromJson(json, DebugResponse.class);
			
			if (response.status != CommandStatus.UNSOLICITED) {
				synchronized(expectLock) {
					while (expect == null)
						try {expectLock.wait();} catch (InterruptedException e) {}
					expect.response(response);
					expect = null;
					expectLock.notifyAll();
				}
			} else {
				System.out.println("UNSOLICITED");
				synchronized(expectLock) {
					unexpectedHandler.response(response);
				}
			}
		}
	}

	public void setUnexpectedHandler(ResponseExpectation unexpectedHandler) {
		synchronized(expectLock) {
			this.unexpectedHandler = unexpectedHandler;
		}
	}
	
	public DebugResponse commandResponse(DebugCommand c) {
		final DebugResponse[] ret = new DebugResponse[]{null};
		final boolean[] done = new boolean[]{false};
		final Object completionLock = new Object();
	
		synchronized(expectLock) {
			while (expect!=null) 
				try {expectLock.wait();} catch (InterruptedException e) {}
			
			expect = new ResponseExpectation() {
				public void response(DebugResponse r) {
					synchronized(completionLock) {
						done[0]=true;
						ret[0] = r;
						completionLock.notifyAll();
					}
				}
			};
			expectLock.notifyAll();
		}
		output.println(new Gson().toJson(c));
		output.flush();
		
		synchronized(completionLock) {
			while (!done[0])
				try {completionLock.wait();} catch (InterruptedException e) {}
		}
		
		return ret[0];
	}

}
