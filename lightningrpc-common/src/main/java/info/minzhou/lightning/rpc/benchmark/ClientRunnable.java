
package info.minzhou.lightning.rpc.benchmark;

import java.util.List;

/**
 * client runnable,so we can collect results
 * 
 */
public interface ClientRunnable extends Runnable {
	
	public List<long[]> getResults();

}
