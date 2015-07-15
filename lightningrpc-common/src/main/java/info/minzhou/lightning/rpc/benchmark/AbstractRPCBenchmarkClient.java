package info.minzhou.lightning.rpc.benchmark;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import info.minzhou.lightning.rpc.Codecs;
import info.minzhou.lightning.rpc.protocol.RPCProtocol;

/**
 * Test for RPC based on reflection Benchmark
 * 
 */
public abstract class AbstractRPCBenchmarkClient extends AbstractBenchmarkClient{

	public ClientRunnable getClientRunnable(String targetIP, int targetPort,
			int clientNums, int rpcTimeout, int codecType, int requestSize,
			CyclicBarrier barrier, CountDownLatch latch, long endTime, long startTime) {
		Map<String, Integer> methodTimeouts = new HashMap<String, Integer>();
		methodTimeouts.put("*", rpcTimeout);
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(targetIP, targetPort));
		String serviceName = "testservice";
		if(codecType == Codecs.PB_CODEC){
			serviceName = "testservicepb";
		}
		return new RPCBenchmarkClientRunnable(
				getProxyInstance(servers, clientNums, 1000, serviceName,methodTimeouts, codecType, RPCProtocol.TYPE), 
				requestSize, barrier, latch, startTime, endTime, codecType);
	}
	
	/*
	 * return ProxyObject
	 */
	public abstract BenchmarkTestService getProxyInstance(
			List<InetSocketAddress> servers, int clientNums,
			int connectTimeout, String targetInstanceName,
			Map<String, Integer> methodTimeouts, int codectype, Integer protocolType);

}
