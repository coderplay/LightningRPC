package info.minzhou.lightning.rpc.netty.serialize;


import info.minzhou.lightning.rpc.protocol.ProtocolUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Encode Message
 *
 */
public class NettyProtocolEncoder extends ChannelOutboundHandlerAdapter {

  public void write(ChannelHandlerContext ctx, Object message, ChannelPromise promise) throws Exception {
    NettyByteBufferWrapper byteBufferWrapper = new NettyByteBufferWrapper(ctx);
    ProtocolUtils.encode(message, byteBufferWrapper);
    ctx.write(byteBufferWrapper.getBuffer(), promise);
  }

}
