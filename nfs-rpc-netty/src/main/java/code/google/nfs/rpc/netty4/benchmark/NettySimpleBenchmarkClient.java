package code.google.nfs.rpc.netty4.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.netty4.client.NettyClientFactory;

/**
 * Netty Direct Call RPC Benchmark Client
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettySimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

	public static void main(String[] args) throws Exception{
		new NettySimpleBenchmarkClient().run(args);
	}
	
	public ClientFactory getClientFactory() {
		return NettyClientFactory.getInstance();
	}

}
