package code.google.nfs.rpc.mina2.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.benchmark.AbstractBenchmarkServer;
import code.google.nfs.rpc.mina2.server.MinaServer;
import code.google.nfs.rpc.server.Server;
/**
 * Mina RPC Benchmark Server
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaBenchmarkServer extends AbstractBenchmarkServer {

	public static void main(String[] args) throws Exception{
		new MinaBenchmarkServer().run(args);
	}
	
	public Server getServer() {
		return new MinaServer();
	}

}
