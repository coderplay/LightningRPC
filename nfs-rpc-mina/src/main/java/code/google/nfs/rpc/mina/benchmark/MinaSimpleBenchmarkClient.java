package code.google.nfs.rpc.mina.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.mina.client.MinaClientFactory;

/**
 * Mina Simple Benchmark Client
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaSimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

	public static void main(String[] args) throws Exception{
		new MinaSimpleBenchmarkClient().run(args);
	}
	
	public ClientFactory getClientFactory() {
		return MinaClientFactory.getInstance();
	}

}
