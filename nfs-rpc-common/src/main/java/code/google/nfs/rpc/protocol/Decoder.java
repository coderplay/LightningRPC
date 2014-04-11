/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
package code.google.nfs.rpc.protocol;
/**
 * Decoder Interface
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public interface Decoder {

	/**
	 * decode byte[] to Object
	 */
	public Object decode(String className,byte[] bytes) throws Exception;
	
}
