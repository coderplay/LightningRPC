package code.google.nfs.rpc.mina2.client;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import code.google.nfs.rpc.client.AbstractClient;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.client.ClientFactory;
/**
 * Mina Client
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClient extends AbstractClient {

	private static final Log LOGGER = LogFactory.getLog(MinaClient.class);

	private static final boolean isWarnEnabled = LOGGER.isWarnEnabled();

	private IoSession session;

	private String key;

	private int connectTimeout;

	public MinaClient(IoSession session, String key, int connectTimeout) {
		this.session = session;
		this.key = key;
		this.connectTimeout = connectTimeout;
	}

	@SuppressWarnings("rawtypes")
	public void sendRequest(final RequestWrapper wrapper,final int timeout)
			throws Exception {
		final long beginTime = System.currentTimeMillis();
		WriteFuture writeFuture = session.write(wrapper);
		final Client self = this;
		writeFuture.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				WriteFuture wfuture = (WriteFuture) future;
				if (wfuture.isWritten()) {
					return;
				}
				String error = "send message to server: "
						+ session.getRemoteAddress()
						+ " error,maybe because sendbuffer is full or connection closed: "
						+ !session.isConnected();
				if(System.currentTimeMillis() - beginTime >= timeout){
					error = "write message to os send buffer timeout,consumetime is: "+(System.currentTimeMillis()-beginTime)+"ms,timeout is:"+timeout;
				}
				LOGGER.error(error);
				ResponseWrapper response = new ResponseWrapper(wrapper.getId(),wrapper.getCodecType(),wrapper.getProtocolType());
				response.setException(new Exception(error));
				try {
					putResponse(response);
				} 
				catch (Exception e) {
					// IGNORE, should not happen
				}
				if (session.isConnected()) {
					if (isWarnEnabled) {
						LOGGER.warn("close the session because send request error,server:"
								+ session.getRemoteAddress());
					}
					session.close(true);
				} 
				else {
					// TODO: exception handle 
					MinaClientFactory.getInstance().removeClient(key, self);
				}
			}
		});
	}

	public String getServerIP() {
		return ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
	}

	public int getServerPort() {
		return ((InetSocketAddress) session.getRemoteAddress()).getPort();
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public long getSendingBytesSize() {
		return session.getScheduledWriteBytes();
	}

	public ClientFactory getClientFactory() {
		return MinaClientFactory.getInstance();
	}

}
