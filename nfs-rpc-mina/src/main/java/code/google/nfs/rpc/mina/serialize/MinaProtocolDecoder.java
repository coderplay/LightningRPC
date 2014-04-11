package code.google.nfs.rpc.mina.serialize;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import code.google.nfs.rpc.protocol.ProtocolUtils;
/**
 * decode receive message
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaProtocolDecoder extends CumulativeProtocolDecoder {
	
	protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
		MinaByteBufferWrapper wrapper = new MinaByteBufferWrapper(in);
		Object returnObject = ProtocolUtils.decode(wrapper, false);
		if(returnObject instanceof Boolean){
			return false;
		}
		out.write(returnObject);
		return true;
	}

}
