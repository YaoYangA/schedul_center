package com.ylm.server.handler;

import com.ylm.common.protobuf.Message;
import com.ylm.common.protobuf.Message.MessageBase;
import com.ylm.server.components.ChannelRepository;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ylm.common.protobuf.Command.CommandType;


/** 其他业务拓展模板参考
  * @Author: myzf
  * @Date: 2019/2/23 13:24
  * @param
*/
@ChannelHandler.Sharable
public class OtherServerHandler extends ChannelInboundHandlerAdapter{
	public Logger log = LoggerFactory.getLogger(this.getClass());

	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");

	private ChannelRepository channelRepository = new ChannelRepository();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message.MessageBase msgBase = (Message.MessageBase)msg;
		log.info(msgBase.getData());
		ChannelFuture cf = ctx.writeAndFlush(
				MessageBase.newBuilder()
				.setClientId(msgBase.getClientId())
				.setCmd(CommandType.UPLOAD_DATA_BACK)
				.setData("这是业务层的逻辑")
				.build()
				);
		/* 上一条消息发送成功后，立马推送一条消息 */
		cf.addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()){
					ctx.writeAndFlush(
							MessageBase.newBuilder()
							.setClientId(msgBase.getClientId())
							.setCmd(CommandType.PUSH_DATA)
							.setData("开始发送业务数据了。。。")
							.build()
							);
				}
			}
		});
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

	}
}
