package com.ylm.server.typeServer;

import com.ylm.server.config.Beans;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCPServer {

	public Logger log = LoggerFactory.getLogger(this.getClass());

	private Beans beans = new Beans();

	private ServerBootstrap serverBootstrap = beans.bootstrap();

	private Channel serverChannel ;

	public void start(String port) throws Exception {
		log.info("服务端启动，端口号:{}",port);
		InetSocketAddress tcpIpAndPort = beans.tcpPort(Integer.parseInt(port));
		serverChannel =  serverBootstrap.bind(tcpIpAndPort).sync().channel().closeFuture().sync().channel();
	}

	public void stop() throws Exception {
		serverChannel.close();
		serverChannel.parent().close();
	}
}
