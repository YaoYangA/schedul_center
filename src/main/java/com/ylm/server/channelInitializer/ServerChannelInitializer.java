package com.ylm.server.channelInitializer;


import com.ylm.common.constant.Constant;
import com.ylm.common.protobuf.Message.MessageBase;
import com.ylm.server.handler.ServerHeartHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    // 实现心跳的hander
    private ServerHeartHandler serverHeartHandler = new ServerHeartHandler();

    // 其他业务拓展模板参考
    private OtherServerHandler otherServerHandler = new OtherServerHandler();
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	ChannelPipeline p = socketChannel.pipeline();
    	//检测空闲必须放在这里 因为pipeline是分顺序加载的
    	p.addLast("idleStateHandler", new IdleStateHandler(Constant.READER_IDLE_TIME_SECONDS
    			, Constant.WRITER_IDLE_TIME_SECONDS, Constant.ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
    	//解码器必须放在前面，否则发数据收不到
        p.addLast(new ProtobufVarint32FrameDecoder());//添加protobuff解码器
        p.addLast(new ProtobufDecoder(MessageBase.getDefaultInstance()));//添加protobuff对应类解码器
        p.addLast(new ProtobufVarint32LengthFieldPrepender());//protobuf的编码器 和上面对对应
        p.addLast(new ProtobufEncoder());//protobuf的编码器

        //自定义的hanlder
        p.addLast("serverHeartHandler", serverHeartHandler);
    }
}
