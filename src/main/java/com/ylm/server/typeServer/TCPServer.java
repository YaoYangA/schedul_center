package com.ylm.server.typeServer;

import com.ylm.http.HttpServer;
import com.ylm.job.QuartzThread;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
public class TCPServer {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void start(String port){
		ServerThread serverThread = new ServerThread();
		serverThread.setPort(Integer.parseInt(port));
		new Thread(serverThread).start();

		QuartzThread quartzThread = new QuartzThread();
		new Thread(quartzThread).start();

		int httpServerPort = Integer.parseInt(port) -1 ;

		log.info("服务端添加定时任务的端口为：{}",httpServerPort);

		HttpServer httpServer = new HttpServer(httpServerPort);
		new Thread(httpServer).start();

	}
}
