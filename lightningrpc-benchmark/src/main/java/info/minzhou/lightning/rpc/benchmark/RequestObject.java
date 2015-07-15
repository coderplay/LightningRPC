package info.minzhou.lightning.rpc.benchmark;

import java.io.Serializable;
/**
 * Just for RPC Benchmark Test,request object
 * 
 */
public class RequestObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] bytes = null;
	
	public RequestObject(int size){
		bytes = new byte[size];
	}

	public byte[] getBytes() {
		return bytes;
	}
	
	
}
