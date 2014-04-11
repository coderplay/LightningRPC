package code.google.nfs.rpc.netty4.client;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import code.google.nfs.rpc.client.AbstractClient;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.client.ClientFactory;

/**
 * Netty Client
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class Netty4Client extends AbstractClient {

	private static final Log LOGGER = LogFactory.getLog(Netty4Client.class);

	private ChannelFuture cf;

	private String key;

	private int connectTimeout;

	public Netty4Client(ChannelFuture cf, String key, int connectTimeout) {
		this.cf = cf;
		this.key = key;
		this.connectTimeout = connectTimeout;
	}

	public void sendRequest(final RequestWrapper wrapper, final int timeout)
			throws Exception {
		final long beginTime = System.currentTimeMillis();
		final Client self = this;
		ChannelFuture writeFuture = cf.channel().writeAndFlush(wrapper);
		// use listener to avoid wait for write & thread context switch
		writeFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					return;
				}
				String errorMsg = "";
				// write timeout
				if (System.currentTimeMillis() - beginTime >= timeout) {
					errorMsg = "write to send buffer consume too long time("
							+ (System.currentTimeMillis() - beginTime)
							+ "),request id is:" + wrapper.getId();
				}
				if (future.isCancelled()) {
					errorMsg = "Send request to " + cf.channel().toString()
							+ " cancelled by user,request id is:"
							+ wrapper.getId();
				}
				if (!future.isSuccess()) {
					if (cf.channel().isOpen()) {
						// maybe some exception,so close the channel
						cf.channel().close();
					} 
					else {
						Netty4ClientFactory.getInstance().removeClient(key, self);
					}
					errorMsg = "Send request to " + cf.channel().toString() + " error" + future.cause();
				}
				LOGGER.error(errorMsg);
				ResponseWrapper response = new ResponseWrapper(wrapper.getId(), wrapper.getCodecType(), wrapper.getProtocolType());
				response.setException(new Exception(errorMsg));
				self.putResponse(response);
			}
		});
	}

	public String getServerIP() {
		return ((InetSocketAddress) cf.channel().remoteAddress()).getHostName();
	}

	public int getServerPort() {
		return ((InetSocketAddress) cf.channel().remoteAddress()).getPort();
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public long getSendingBytesSize() {
		// TODO: implement it
		return 0;
	}

	public ClientFactory getClientFactory() {
		return Netty4ClientFactory.getInstance();
	}

}
