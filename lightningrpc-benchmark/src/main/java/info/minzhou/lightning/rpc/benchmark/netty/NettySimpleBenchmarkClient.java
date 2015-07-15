package info.minzhou.lightning.rpc.benchmark.netty;


import info.minzhou.lightning.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import info.minzhou.lightning.rpc.client.ClientFactory;
import info.minzhou.lightning.rpc.netty.client.NettyClientFactory;

/**
 * Netty Direct Call RPC Benchmark Client
 *
 */
public class NettySimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

  public static void main(String[] args) throws Exception {
    new NettySimpleBenchmarkClient().run(args);
  }

  public ClientFactory getClientFactory() {
    return NettyClientFactory.getInstance();
  }

}
