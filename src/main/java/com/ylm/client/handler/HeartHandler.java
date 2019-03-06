package com.ylm.client.handler;

import com.ylm.client.heart.NettyClient;
import com.ylm.common.protobuf.Command;
import com.ylm.common.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class HeartHandler extends SimpleChannelInboundHandler<Message.MessageBase> {
	public Logger log = LoggerFactory.getLogger(this.getClass());
	private final DateFormat sf = new SimpleDateFormat("HH:mm:ss");

	private NettyClient nettyClient;
	private final static String CLIENTID = "test";
	private long ccTime = 0;//缓存发送时间 单位毫秒

	// 定义客户端没有收到服务端的pong消息的最大次数
	private static final int MAX_UN_REC_PONG_TIMES = 3;

	// 多长时间未请求后，发送心跳
	private static final int WRITE_WAIT_SECONDS = 5;//暂时未使用

	// 隔N秒后重连
	private static final int RE_CONN_WAIT_SECONDS = 5;//暂时未使用

	// 客户端连续N次没有收到服务端的pong消息  计数器
	private int unRecPongTimes = 0 ;
	/**
	 * @param nettyClient
	 */
	public HeartHandler(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if(unRecPongTimes < MAX_UN_REC_PONG_TIMES){
				sendPingMsg(ctx,CLIENTID);
				unRecPongTimes++;
			}else{
				log.info("连接服务器失败，启动定时任务");
				ctx.channel().close();
			}
			log.info("超时类型："+event.state());
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	/**
	 * 发送ping消息
	 * @param context
	 */
	protected void sendPingMsg(ChannelHandlerContext context,String client) {
		log.info("发送ping消息");
		ccTime = System.currentTimeMillis();
		context.writeAndFlush(
				Message.MessageBase.newBuilder()
				.setClientId(client)
				.setCmd(Command.CommandType.PING)
				.setData(String.valueOf(ccTime))
				.build()
				);
	}

	/**
	 * @return返回微秒 如果有更深一步的业务可使用
	 */
	public static Long getmicTime() {
		Long cutime = System.currentTimeMillis() * 1000; // 微秒
		Long nanoTime = System.nanoTime(); // 纳秒
		return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
	}

	/**
	 * 处理断开重连
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("检测到心跳服务器断开！！！");
		final EventLoop eventLoop = ctx.channel().eventLoop();  
		eventLoop.schedule(() -> nettyClient.doConnect(new Bootstrap(), eventLoop), 10L, TimeUnit.SECONDS);
		super.channelInactive(ctx);  
	}



	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message.MessageBase msg) throws Exception {
		if(msg.getCmd().equals(Command.CommandType.AUTH_BACK)){
			log.info(msg.getData());
			ctx.writeAndFlush(
					Message.MessageBase.newBuilder()
							.setClientId(CLIENTID)
							.setCmd(Command.CommandType.PUSH_DATA)
							.setData("发送业务数据中。。。")
							.build()
			);
		}else if(msg.getCmd().equals(Command.CommandType.PING)){
			//接收到server发送的ping指令
			log.info(msg.getData());

		}else if(msg.getCmd().equals(Command.CommandType.PONG)){
			//接收到server发送的pong指令
			unRecPongTimes = 0;
			//计算ping值
			long ping = (System.currentTimeMillis()-ccTime)/2;
			log.info("客户端和服务器的ping是"+ping+"ms");
			//log.info(msg.getData());
			//计算此时的时间
			//	log.info(msg.getData());
		}else if(msg.getCmd().equals(Command.CommandType.PUSH_DATA)){
			//接收到server推送数据
			log.info(msg.getData());

		}else if(msg.getCmd().equals(Command.CommandType.PUSH_DATA_BACK)){
			//接收到server返回数据
			log.info(msg.getData());

		}else{
			log.info(msg.getData());
		}
	}
}
