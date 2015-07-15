
package info.minzhou.lightning.rpc.protocol;
/**
 * Encoder Interface
 * 
 */
public interface Encoder {

	/**
	 * Encode Object to byte[]
	 */
	public byte[] encode(Object object) throws Exception;
	
}
