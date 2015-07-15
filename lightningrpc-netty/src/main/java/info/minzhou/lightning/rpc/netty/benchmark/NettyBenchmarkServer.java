package info.minzhou.lightning.rpc.netty.benchmark;


import info.minzhou.lightning.rpc.benchmark.AbstractBenchmarkServer;
import info.minzhou.lightning.rpc.netty.server.NettyServer;
import info.minzhou.lightning.rpc.server.Server;

/**
 * Netty RPC Benchmark Server
 *
 */
public class NettyBenchmarkServer extends AbstractBenchmarkServer {

  public static void main(String[] args) throws Exception {
    new NettyBenchmarkServer().run(args);
  }

  public Server getServer() {
    return new NettyServer();
  }

}
