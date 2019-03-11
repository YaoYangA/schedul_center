package com.ylm.client.heart;

import com.ylm.client.handler.HeartHandler;
import com.ylm.client.handler.LogicClientHandler;
import com.ylm.common.protobuf.Message;
import com.ylm.http.HttpServer;
import com.ylm.job.QuartzThread;
import com.ylm.util.QuartzUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * netty客户端
 * @author lenovo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyClient {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private final static String HOST = "127.0.0.1";
	private int PORT = 19999;
	private final static int READER_IDLE_TIME_SECONDS = 0;//读操作空闲20秒
	private final static int WRITER_IDLE_TIME_SECONDS = 5;//写操作空闲20秒
	private final static int ALL_IDLE_TIME_SECONDS = 0;//读写全部空闲40秒

	// 通信失败次数
	private Integer failCount = 0;

	private EventLoopGroup loop = new NioEventLoopGroup();

	public void run(String port) throws Exception {
		try {

		    if (port != null){
		        PORT = Integer.parseInt(port);
            }
			// 启动添加定时任务的HttpServer
			log.info("客户端添加定时任务，HTTP服务的端口为：{}",PORT+1);
			HttpServer httpServer = new HttpServer(PORT+1);
			new Thread(httpServer).start();

			log.info("Client 启动");
			doConnect(new Bootstrap(), loop);
		}catch (Exception e) {
			log.error("Client 出现错误，错误信息：{}",e.getMessage());
		}
	}

	/**
	 * netty client 连接，连接失败5秒后重试连接
	 */
	public Bootstrap doConnect(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
		try {
			if (bootstrap != null) {
				bootstrap.group(eventLoopGroup);
				bootstrap.channel(NioSocketChannel.class);
				bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();

						p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS
								, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));

						p.addLast(new ProtobufVarint32FrameDecoder());
						p.addLast(new ProtobufDecoder(Message.MessageBase.getDefaultInstance()));

						p.addLast(new ProtobufVarint32LengthFieldPrepender());
						p.addLast(new ProtobufEncoder());

						p.addLast("clientHandler", new LogicClientHandler());
						p.addLast("idleTimeoutHandler", new HeartHandler(NettyClient.this));

					}
				});
				bootstrap.remoteAddress(HOST, PORT);
				ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener)->{
					final EventLoop eventLoop = futureListener.channel().eventLoop();
					if (!futureListener.isSuccess()) {
						failCount++;
						log.warn("连接服务器失败，5s后重新尝试连接！重连次数：{}",failCount);
						futureListener.channel().eventLoop().schedule(() -> doConnect(new Bootstrap(), eventLoop), 5, TimeUnit.SECONDS);
					}else{
						failCount = 0;
						log.info("连接服务器成功，客户端定时任务停止，服务端定时任务启动");
						QuartzUtils quartzUtils = new QuartzUtils();
						Scheduler scheduler = quartzUtils.getScheduler();
						scheduler.standby();
					}
					if(failCount==3){
						log.info("重连服务端失败，启动定时任务");
						QuartzThread thread = new QuartzThread();
						new Thread(thread).start();
					}
				});
				f.channel().closeFuture().sync();
				//eventLoopGroup.shutdownGracefully();
			}
		} catch (InterruptedException e) {
			log.error("客户端连接出现异常，异常信息：{}",e.getMessage());
		}
		return bootstrap;
	}
}
