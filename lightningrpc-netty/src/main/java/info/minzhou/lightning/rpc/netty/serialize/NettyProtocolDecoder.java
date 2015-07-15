package info.minzhou.lightning.rpc.netty.serialize;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * decode byte[]
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettyProtocolDecoder extends ChannelInboundHandlerAdapter {

  ByteBuf cumulation;
  private boolean singleDecode;
  private boolean decodeWasNull;

  /**
   * <p>
   * If set then only one message is decoded on each {@link #channelRead(ChannelHandlerContext, Object)}
   * call. This may be useful if you need to do some protocol upgrade and want to make sure nothing is mixed up.
   * </p>
   * Default is {@code false} as this has performance impacts.
   */
  public void setSingleDecode(boolean singleDecode) {
    this.singleDecode = singleDecode;
  }

  /**
   * If {@code true} then only one message is decoded on each
   * {@link #channelRead(ChannelHandlerContext, Object)} call.
   * Default is {@code false} as this has performance impacts.
   */
  public boolean isSingleDecode() {
    return singleDecode;
  }

  /**
   * Returns the actual number of readable bytes in the internal cumulative
   * buffer of this decoder. You usually do not need to rely on this value
   * to write a decoder. Use it only when you must use it at your own risk.
   * This method is a shortcut to {@link #internalBuffer() internalBuffer().readableBytes()}.
   */
  protected int actualReadableBytes() {
    return internalBuffer().readableBytes();
  }

  /**
   * Returns the internal cumulative buffer of this decoder. You usually
   * do not need to access the internal buffer directly to write a decoder.
   * Use it only when you must use it at your own risk.
   */
  protected ByteBuf internalBuffer() {
    if (cumulation != null) {
      return cumulation;
    } else {
      return Unpooled.EMPTY_BUFFER;
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RecyclableArrayList out = RecyclableArrayList.newInstance();
    try {
      if (msg instanceof ByteBuf) {
        ByteBuf data = (ByteBuf) msg;
        if (cumulation == null) {
          cumulation = data;
          try {
            callDecode(ctx, cumulation, out);
          } finally {
            if (cumulation != null && !cumulation.isReadable()) {
              cumulation.release();
              cumulation = null;
            }
          }
        } else {
          try {
            if (cumulation.writerIndex() > cumulation.maxCapacity() - data.readableBytes()) {
              ByteBuf oldCumulation = cumulation;
              cumulation = ctx.alloc().buffer(oldCumulation.readableBytes() + data.readableBytes());
              cumulation.writeBytes(oldCumulation);
              oldCumulation.release();
            }
            cumulation.writeBytes(data);
            callDecode(ctx, cumulation, out);
          } finally {
            if (cumulation != null) {
              if (!cumulation.isReadable()) {
                cumulation.release();
                cumulation = null;
              } else {
                cumulation.discardSomeReadBytes();
              }
            }
            data.release();
          }
        }
      } else {
        out.add(msg);
      }
    } catch (DecoderException e) {
      throw e;
    } catch (Throwable t) {
      throw new DecoderException(t);
    } finally {
      if (out.isEmpty()) {
        decodeWasNull = true;
      }

      List<Object> results = new ArrayList<Object>();
      for (Object result : out) {
        results.add(result);
      }
      ctx.fireChannelRead(results);

      out.recycle();
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    if (decodeWasNull) {
      decodeWasNull = false;
      if (!ctx.channel().config().isAutoRead()) {
        ctx.read();
      }
    }
    ctx.fireChannelReadComplete();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    RecyclableArrayList out = RecyclableArrayList.newInstance();
    try {
      if (cumulation != null) {
        callDecode(ctx, cumulation, out);
        decodeLast(ctx, cumulation, out);
      } else {
        decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
      }
    } catch (DecoderException e) {
      throw e;
    } catch (Exception e) {
      throw new DecoderException(e);
    } finally {
      if (cumulation != null) {
        cumulation.release();
        cumulation = null;
      }

      for (int i = 0; i < out.size(); i++) {
        ctx.fireChannelRead(out.get(i));
      }
      ctx.fireChannelInactive();
    }
  }

  @Override
  public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    ByteBuf buf = internalBuffer();
    int readable = buf.readableBytes();
    if (buf.isReadable()) {
      ByteBuf bytes = buf.readBytes(readable);
      buf.release();
      ctx.fireChannelRead(bytes);
    }
    cumulation = null;
    ctx.fireChannelReadComplete();
    handlerRemoved0(ctx);
  }

  /**
   * Gets called after the {@link ByteToMessageDecoder} was removed from the actual context and it doesn't handle
   * events anymore.
   */
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * Called once data should be decoded from the given {@link ByteBuf}. This method will call
   * {@link #decode(ChannelHandlerContext, ByteBuf, List)} as long as decoding should take place.
   *
   * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
   * @param in  the {@link ByteBuf} from which to read data
   * @param out the {@link List} to which decoded messages should be added
   */
  protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    try {
      while (in.isReadable()) {
        int outSize = out.size();
        int oldInputLength = in.readableBytes();
        decode(ctx, in, out);

        // Check if this handler was removed before try to continue the loop.
        // If it was removed it is not safe to continue to operate on the buffer
        //
        // See https://github.com/netty/netty/issues/1664
        if (ctx.isRemoved()) {
          break;
        }

        if (outSize == out.size()) {
          if (oldInputLength == in.readableBytes()) {
            break;
          } else {
            continue;
          }
        }

        if (oldInputLength == in.readableBytes()) {
          throw new DecoderException(
              StringUtil.simpleClassName(getClass()) +
                  ".decode() did not read anything but decoded a message.");
        }

        if (isSingleDecode()) {
          break;
        }
      }
    } catch (DecoderException e) {
      throw e;
    } catch (Throwable cause) {
      throw new DecoderException(cause);
    }
  }

  protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
    NettyByteBufferWrapper wrapper = new NettyByteBufferWrapper(buf);
    Object result = ProtocolUtils.decode(wrapper, null);
    if (result != null) {
      out.add(result);
    }
  }

  /**
   * Is called one last time when the {@link ChannelHandlerContext} goes in-active. Which means the
   * {@link #channelInactive(ChannelHandlerContext)} was triggered.
   * By default this will just call {@link #decode(ChannelHandlerContext, ByteBuf, List)} but sub-classes may
   * override this for some special cleanup operation.
   */
  protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    decode(ctx, in, out);
  }

}
