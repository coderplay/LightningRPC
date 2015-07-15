
package info.minzhou.lightning.rpc.benchmark;


/**
 * Just for Reflection RPC Benchmark
 * 
 */
public class BenchmarkTestServiceImpl implements BenchmarkTestService {

	private int responseSize;
	
	public BenchmarkTestServiceImpl(int responseSize){
		this.responseSize = responseSize;
	}
	
	// support java/hessian/pb codec
	public Object execute(Object request) {
		return new ResponseObject(responseSize);
	}

	public Object executePB(PB.RequestObject request) {
		throw new UnsupportedOperationException("unsupported");
	}

}
