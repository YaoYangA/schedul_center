package com.ylm.server.typeServer;

import com.ylm.job.QuartzThread;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TCPServer {

	public void start(String port){
		ServerThread serverThread = new ServerThread();
		serverThread.setPort(Integer.parseInt(port));
		new Thread(serverThread).start();

		QuartzThread quartzThread = new QuartzThread();
		new Thread(quartzThread).start();

	}
}
