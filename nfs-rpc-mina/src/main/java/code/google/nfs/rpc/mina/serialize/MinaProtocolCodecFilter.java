package code.google.nfs.rpc.mina.serialize;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.support.BaseIoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Encoder & Decoder Filter for mina
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaProtocolCodecFilter extends ProtocolCodecFilter {

	public MinaProtocolCodecFilter() {
		super(new MinaProtocolEncoder(), new MinaProtocolDecoder());
	}

	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		session.setAttribute(ProtocolCodecFilter.class.getName()+ ".decoderOut", 
							 new MinaProtocolDecoderOutput(session,nextFilter));
		super.messageReceived(nextFilter, session, message);
	}

	class MinaProtocolDecoderOutput implements ProtocolDecoderOutput {

		private final NextFilter nextFilter;

		private final IoSession session;

		private final List<Object> messageQueue = new ArrayList<Object>();

		public MinaProtocolDecoderOutput(IoSession session,
				NextFilter nextFilter) {
			this.nextFilter = nextFilter;
			this.session = session;
		}

		public void flush() {
			nextFilter.messageReceived(session, messageQueue);
		}

		public void write(Object message) {
			messageQueue.add(message);
			if (session instanceof BaseIoSession) {
				((BaseIoSession) session).increaseReadMessages();
			}
		}
	}

}
