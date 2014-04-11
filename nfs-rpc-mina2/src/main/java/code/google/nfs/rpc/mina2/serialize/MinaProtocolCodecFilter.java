package code.google.nfs.rpc.mina2.serialize;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Encoder & Decoder Filter for mina
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaProtocolCodecFilter extends ProtocolCodecFilter {

	private final AttributeKey DECODER_OUT = new AttributeKey(ProtocolCodecFilter.class, "decoderOut");
	
	public MinaProtocolCodecFilter() {
		super(new MinaProtocolEncoder(), new MinaProtocolDecoder());
	}

	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		session.setAttribute(DECODER_OUT,new MinaProtocolDecoderOutput(session));
		super.messageReceived(nextFilter, session, message);
	}

	class MinaProtocolDecoderOutput implements ProtocolDecoderOutput {

		private final IoSession session;

		private final List<Object> messageQueue = new ArrayList<Object>();

		public MinaProtocolDecoderOutput(IoSession session) {
			this.session = session;
		}

		public void write(Object message) {
			messageQueue.add(message);
			if (session instanceof AbstractIoSession) {
				((AbstractIoSession) session).increaseReadMessages(System.currentTimeMillis());
			}
		}
		
		public void flush(NextFilter nextFilter, IoSession session) {
			nextFilter.messageReceived(session, messageQueue);
		}
	}

}
