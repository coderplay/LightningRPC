package info.minzhou.lightning.rpc.protocol;

import info.minzhou.lightning.rpc.benchmark.KryoUtils;

import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo Encoder
 * 
 */
public class KryoEncoder implements Encoder {
	/**
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@Override
	public byte[] encode(Object object) throws Exception {
		Output output = new Output(256);
		KryoUtils.getKryo().writeClassAndObject(output, object);
		return output.toBytes();
	}

}
