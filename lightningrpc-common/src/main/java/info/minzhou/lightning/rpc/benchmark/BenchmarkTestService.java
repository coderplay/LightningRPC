
package info.minzhou.lightning.rpc.benchmark;
/**
 * Just for Reflection RPC Benchmark
 * 
 */
public interface BenchmarkTestService {

	public Object execute(Object request);
	
	public Object executePB(PB.RequestObject request);
	
}
