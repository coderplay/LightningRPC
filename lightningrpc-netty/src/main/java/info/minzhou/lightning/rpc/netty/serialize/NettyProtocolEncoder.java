package info.minzhou.lightning.rpc.netty.serialize;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.protocol.ProtocolUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Encode Message
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettyProtocolEncoder extends ChannelOutboundHandlerAdapter {

  public void write(ChannelHandlerContext ctx, Object message, ChannelPromise promise) throws Exception {
    NettyByteBufferWrapper byteBufferWrapper = new NettyByteBufferWrapper(ctx);
    ProtocolUtils.encode(message, byteBufferWrapper);
    ctx.write(byteBufferWrapper.getBuffer(), promise);
  }

}
