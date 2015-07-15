package info.minzhou.lightning.rpc.netty4.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import info.minzhou.lightning.rpc.client.ClientFactory;
import info.minzhou.lightning.rpc.netty4.client.Netty4ClientFactory;

/**
 * Netty Direct Call RPC Benchmark Client
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4SimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

  public static void main(String[] args) throws Exception {
    new Netty4SimpleBenchmarkClient().run(args);
  }

  public ClientFactory getClientFactory() {
    return Netty4ClientFactory.getInstance();
  }

}
