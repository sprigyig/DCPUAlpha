package debug;

public class DebugResponse {
	public CommandStatus status;
	public String userAlert;
	public long[] payload;
	public DebugResponse(CommandStatus status, String userAlert, long[] payload) {
		this.status = status;
		this.userAlert = userAlert;
		this.payload = payload;
	}
	public DebugResponse(){}
}
