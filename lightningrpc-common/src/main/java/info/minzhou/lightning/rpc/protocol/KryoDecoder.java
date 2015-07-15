package info.minzhou.lightning.rpc.protocol;

import com.esotericsoftware.kryo.io.Input;

/**
 * Kryo Decoder
 * 
 */
public class KryoDecoder implements Decoder {
	/**
	 * @param className
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Input input = new Input(bytes);
		return KryoUtils.getKryo().readClassAndObject(input);
	}
}
