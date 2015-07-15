package info.minzhou.lightning.rpc.server;

import info.minzhou.lightning.rpc.RequestWrapper;
import info.minzhou.lightning.rpc.ResponseWrapper;

/**
 * Server Handler interface,when server receive message,it will handle 
 * 
 */
public interface ServerHandler {

	/**
	 * register business handler,provide for Server
	 */
	public void registerProcessor(String instanceName, Object instance);

	/**
	 * handle the request
	 */
	public ResponseWrapper handleRequest(final RequestWrapper request);

}