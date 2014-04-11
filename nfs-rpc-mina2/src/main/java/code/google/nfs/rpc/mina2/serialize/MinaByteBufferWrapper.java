package code.google.nfs.rpc.mina2.serialize;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import org.apache.mina.core.buffer.IoBuffer;

import code.google.nfs.rpc.protocol.ByteBufferWrapper;
/**
 * Implements ByteBufferWrapper based on ByteBuffer
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaByteBufferWrapper implements ByteBufferWrapper {

	private IoBuffer byteBuffer;
	
	public MinaByteBufferWrapper(){
		;
	}
	
	public MinaByteBufferWrapper(IoBuffer in){
		this.byteBuffer = in;
	}
	
	public ByteBufferWrapper get(int capacity) {
		byteBuffer = IoBuffer.allocate(capacity,false);
		return this;
	}

	public byte readByte() {
		return byteBuffer.get();
	}

	public void readBytes(byte[] dst) {
		byteBuffer.get(dst);
	}

	public int readInt() {
		return byteBuffer.getInt();
	}

	public int readableBytes() {
		return byteBuffer.remaining();
	}

	public int readerIndex() {
		return byteBuffer.position();
	}

	public void setReaderIndex(int index) {
		byteBuffer.position(index);
	}

	public void writeByte(byte data) {
		byteBuffer.put(data);
	}

	public void writeBytes(byte[] data) {
		byteBuffer.put(data);
	}

	public void writeInt(int data) {
		byteBuffer.putInt(data);
	}
	
	public IoBuffer getByteBuffer(){
		return byteBuffer;
	}

	public void writeByte(int index, byte data) {
		byteBuffer.put(index,data);
	}

}
