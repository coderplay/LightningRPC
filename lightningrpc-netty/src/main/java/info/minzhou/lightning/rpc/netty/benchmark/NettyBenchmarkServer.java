package info.minzhou.lightning.rpc.netty.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.benchmark.AbstractBenchmarkServer;
import info.minzhou.lightning.rpc.netty.server.NettyServer;
import info.minzhou.lightning.rpc.server.Server;

/**
 * Netty RPC Benchmark Server
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettyBenchmarkServer extends AbstractBenchmarkServer {

  public static void main(String[] args) throws Exception {
    new NettyBenchmarkServer().run(args);
  }

  public Server getServer() {
    return new NettyServer();
  }

}
