package code.google.nfs.rpc.netty4.client;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.client.AbstractClientInvocationHandler;
import code.google.nfs.rpc.client.ClientFactory;
/**
 * Netty Client Invocation Handler for Client Proxy
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class Netty4ClientInvocationHandler extends
		AbstractClientInvocationHandler {

	public Netty4ClientInvocationHandler(List<InetSocketAddress> servers,
                                       int clientNums, int connectTimeout, String targetInstanceName,
                                       Map<String, Integer> methodTimeouts, int codectype, Integer protocolType) {
		super(servers, clientNums, connectTimeout, targetInstanceName, methodTimeouts,codectype,protocolType);
	}

	public ClientFactory getClientFactory() {
		return Netty4ClientFactory.getInstance();
	}

}
