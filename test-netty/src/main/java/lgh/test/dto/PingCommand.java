package lgh.test.dto;

import lgh.test.constant.CommandCode;
import lgh.test.util.IDGenerator;

public class PingCommand extends Command {

	public PingCommand() {
		requestId=IDGenerator.getRequestId();
		commandCode=CommandCode.PING_REQ;
	}

	
}
