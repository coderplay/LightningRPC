package info.minzhou.lightning.rpc.netty.client;


import info.minzhou.lightning.rpc.client.AbstractClientInvocationHandler;
import info.minzhou.lightning.rpc.client.ClientFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Netty Client Invocation Handler for Client Proxy
 *
 */
public class NettyClientInvocationHandler extends
    AbstractClientInvocationHandler {

  public NettyClientInvocationHandler(List<InetSocketAddress> servers,
                                      int clientNums, int connectTimeout, String targetInstanceName,
                                      Map<String, Integer> methodTimeouts, int codectype, Integer protocolType) {
    super(servers, clientNums, connectTimeout, targetInstanceName, methodTimeouts, codectype, protocolType);
  }

  public ClientFactory getClientFactory() {
    return NettyClientFactory.getInstance();
  }

}
