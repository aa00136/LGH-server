package lgh.test.dto;

import lgh.test.constant.CommandCode;

public class PingCommandResponse extends Command {

	public PingCommandResponse(int requestId) {
		this.requestId=requestId;
		this.commandCode=CommandCode.PING_RSP;
		this.responseCode=1;
	}
}
