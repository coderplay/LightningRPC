package code.google.nfs.rpc.mina.client;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.mina.serialize.MinaProtocolCodecFilter;

/**
 * Mina Client Factory
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClientFactory extends AbstractClientFactory {

	private static Log LOGGER = LogFactory.getLog(MinaClientFactory.class);

	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	private static final int processorCount = Runtime.getRuntime().availableProcessors() + 1;

	private static final String CONNECTOR_THREADNAME = "MINACLIENT";

	private static final ThreadFactory CONNECTOR_TFACTORY = new NamedThreadFactory(CONNECTOR_THREADNAME);

	private static final AbstractClientFactory _self = new MinaClientFactory();

	private SocketConnector ioConnector;

	private MinaClientFactory() {
		// only one ioConnector,avoid create too many io processor thread
		ioConnector = new SocketConnector(processorCount,
				Executors.newCachedThreadPool(CONNECTOR_TFACTORY));
	}

	public static AbstractClientFactory getInstance() {
		return _self;
	}

	protected Client createClient(String targetIP, int targetPort,
			int connectTimeout, String key) throws Exception {
		if (isDebugEnabled) {
			LOGGER.debug("create connection to :" + targetIP + ":" + targetPort
					+ ",timeout is:" + connectTimeout + ",key is:" + key);
		}
		SocketConnectorConfig cfg = new SocketConnectorConfig();
		cfg.setThreadModel(ThreadModel.MANUAL);
		if (connectTimeout > 1000) {
			cfg.setConnectTimeout((int) connectTimeout / 1000);
		} else {
			cfg.setConnectTimeout(1);
		}
		cfg.getSessionConfig().setTcpNoDelay(Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
		cfg.getFilterChain().addLast("objectserialize",new MinaProtocolCodecFilter());
		SocketAddress targetAddress = new InetSocketAddress(targetIP,targetPort);
		MinaClientProcessor processor = new MinaClientProcessor(this, key);
		ConnectFuture connectFuture = ioConnector.connect(targetAddress, null,processor, cfg);
		// wait for connection established
		connectFuture.join();

		IoSession ioSession = connectFuture.getSession();
		if ((ioSession == null) || (!ioSession.isConnected())) {
			String targetUrl = targetIP + ":" + targetPort;
			LOGGER.error("create connection error,targetaddress is " + targetUrl);
			throw new Exception("create connection error,targetaddress is " + targetUrl);
		}
		if (isDebugEnabled) {
			LOGGER.debug("create connection to :" + targetIP + ":" + targetPort
					+ ",timeout is:" + connectTimeout + ",key is:" + key
					+ " successed");
		}
		MinaClient client = new MinaClient(ioSession, key, connectTimeout);
		processor.setClient(client);
		return client;
	}

}
