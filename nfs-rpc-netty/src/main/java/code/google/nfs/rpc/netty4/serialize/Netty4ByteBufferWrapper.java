package code.google.nfs.rpc.netty4.serialize;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import code.google.nfs.rpc.protocol.ByteBufferWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Implements ByteBufferWrapper based on Netty ChannelBuffer
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ByteBufferWrapper implements ByteBufferWrapper {

  private ByteBuf buffer;

  private ChannelHandlerContext ctx;

  public Netty4ByteBufferWrapper() {
    ;
  }

  public Netty4ByteBufferWrapper(ByteBuf in) {
    buffer = in;
  }

  public Netty4ByteBufferWrapper(ChannelHandlerContext ctx) {
    this.ctx = ctx;
  }

  public ByteBufferWrapper get(int capacity) {
    if (buffer != null)
      return this;
    buffer = ctx.alloc().buffer(capacity);
    return this;
  }

  public byte readByte() {
    return buffer.readByte();
  }

  public void readBytes(byte[] dst) {
    buffer.readBytes(dst);
  }

  public int readInt() {
    return buffer.readInt();
  }

  public int readableBytes() {
    return buffer.readableBytes();
  }

  public int readerIndex() {
    return buffer.readerIndex();
  }

  public void setReaderIndex(int index) {
    buffer.setIndex(index, buffer.writerIndex());
  }

  public void writeByte(byte data) {
    buffer.writeByte(data);
  }

  public void writeBytes(byte[] data) {
    buffer.writeBytes(data);
  }

  public void writeInt(int data) {
    buffer.writeInt(data);
  }

  public ByteBuf getBuffer() {
    return buffer;
  }

  public void writeByte(int index, byte data) {
    buffer.writeByte(data);
  }

}
