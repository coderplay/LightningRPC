package code.google.nfs.rpc.mina2.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.benchmark.AbstractRPCBenchmarkClient;
import code.google.nfs.rpc.benchmark.BenchmarkTestService;
import code.google.nfs.rpc.mina2.client.MinaClientInvocationHandler;

/**
 * Mina RPC BenchmarkClient
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaRPCBenchmarkClient extends AbstractRPCBenchmarkClient {

	public static void main(String[] args) throws Exception {
		new MinaRPCBenchmarkClient().run(args);
	}

	public BenchmarkTestService getProxyInstance(
			List<InetSocketAddress> servers, int clientNums,
			int connectTimeout, String targetInstanceName,
			Map<String, Integer> methodTimeouts, int codectype, Integer protocolType) {
		return (BenchmarkTestService) Proxy.newProxyInstance(
				MinaRPCBenchmarkClient.class.getClassLoader(),
				new Class<?>[] { BenchmarkTestService.class },
				new MinaClientInvocationHandler(servers, clientNums,
						connectTimeout, targetInstanceName, methodTimeouts,codectype, protocolType));
	}

}
