package com.example.projectUav.common.TransmitNetty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * 客户端（基于Netty框架）
 */
public class ClientNetty {
    public static Channel channel = null;
    public void setClientNetty(String inetHost,int inetPort) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        this.channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                ByteBuf buffer = (ByteBuf) msg;
                                System.out.println("发送到安卓端的消息："+buffer.toString(Charset.defaultCharset()));
                                
                                ByteBuf buf = ctx.alloc().buffer();
                                buf.writeBytes(buffer);
                                ctx.writeAndFlush(buf);
                            }
                        });
                    }
                }).connect(inetHost, inetPort).sync().channel();
    
        this.channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });
        
    }
}
