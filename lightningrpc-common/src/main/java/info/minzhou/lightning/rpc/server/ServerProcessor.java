
package info.minzhou.lightning.rpc.server;
/**
 * Direct RPC Call Server Processor Interface
 * 
 */
public interface ServerProcessor {

	/**
	 * Handle request,then return Object
	 * 
	 * @param request
	 * @return Object
	 * @throws Exception
	 */
	public Object handle(Object request) throws Exception;
	
}
