package info.minzhou.lightning.rpc.netty.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import info.minzhou.lightning.rpc.client.ClientFactory;
import info.minzhou.lightning.rpc.netty.client.NettyClientFactory;

/**
 * Netty Direct Call RPC Benchmark Client
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettySimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

  public static void main(String[] args) throws Exception {
    new NettySimpleBenchmarkClient().run(args);
  }

  public ClientFactory getClientFactory() {
    return NettyClientFactory.getInstance();
  }

}
