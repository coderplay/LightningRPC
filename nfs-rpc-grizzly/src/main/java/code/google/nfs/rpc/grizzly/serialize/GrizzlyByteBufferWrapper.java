package code.google.nfs.rpc.grizzly.serialize;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.memory.MemoryManager;

import code.google.nfs.rpc.protocol.ByteBufferWrapper;
import org.glassfish.grizzly.memory.Buffers;

/**
 * Grizzly ByteBuffer Wrapper
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyByteBufferWrapper implements ByteBufferWrapper {

	private Buffer buffer;
	private FilterChainContext ctx;
	
    public GrizzlyByteBufferWrapper(FilterChainContext ctx) {
		this.ctx = ctx;
	}
	
    public GrizzlyByteBufferWrapper(Buffer buffer) {
		this.buffer = buffer;
	}
	
	public ByteBufferWrapper get(int capacity) {
		buffer = Buffers.wrap(ctx.getMemoryManager(), new byte[capacity]);
		return this;
	}
	
    public Buffer getBuffer() {
		return buffer;
	}

	public byte readByte() {
		return buffer.get();
	}

	public void readBytes(byte[] data) {
		buffer.get(data);
	}

	public int readInt() {
		return buffer.getInt();
	}

	public int readableBytes() {
		return buffer.remaining();
	}

	public int readerIndex() {
        return buffer.position();
	}

	public void setReaderIndex(int readerIndex) {
        buffer.position(readerIndex);
	}

	public void writeByte(byte data) {
		buffer.put(data);
	}

	public void writeByte(int index, byte data) {
		buffer.put(index, data);
	}

	public void writeBytes(byte[] data) {
		buffer.put(data);
	}

	public void writeInt(int data) {
		buffer.putInt(data);
	}
}
