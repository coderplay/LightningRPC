
package info.minzhou.lightning.rpc.protocol;
/**
 * Decoder Interface
 * 
 */
public interface Decoder {

    /**
     * decode byte[] to Object
     */
    public Object decode(String className,byte[] bytes) throws Exception;

}
