package info.minzhou.lightning.rpc.server;

import java.util.concurrent.ExecutorService;
/**
 * RPC Server Interface
 * 
 */
public interface Server {

	/**
	 * start server at listenPort,requests will be handled in businessThreadPool
	 */
	public void start(int listenPort,ExecutorService businessThreadPool) throws Exception;
	
	/**
	 * register business handler
	 */
	public void registerProcessor(int protocolType,String serviceName,Object serviceInstance);
	
	/**
	 * stop server
	 */
	public void stop() throws Exception;
	
}
