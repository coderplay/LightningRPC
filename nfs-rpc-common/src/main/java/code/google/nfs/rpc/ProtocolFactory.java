package code.google.nfs.rpc;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.protocol.Protocol;
import code.google.nfs.rpc.protocol.RPCProtocol;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;
import code.google.nfs.rpc.server.RPCServerHandler;
import code.google.nfs.rpc.server.ServerHandler;
import code.google.nfs.rpc.server.SimpleProcessorServerHandler;

/**
 * Protocol Factory,for set Protocol class and serverHandler class
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class ProtocolFactory {
	
//	private static final Log LOGGER = LogFactory.getLog(ProtocolFactory.class);
	
	private static Protocol[] protocolHandlers = new Protocol[5];
	
	private static ServerHandler[] serverHandlers = new ServerHandler[5];
	
	static{
		registerProtocol(RPCProtocol.TYPE, new RPCProtocol(), new RPCServerHandler());
		registerProtocol(SimpleProcessorProtocol.TYPE, new SimpleProcessorProtocol(), new SimpleProcessorServerHandler());
	}
	
	public static void registerProtocol(int type,Protocol customProtocol,ServerHandler customServerHandler){
		if(type > protocolHandlers.length){
			Protocol[] newProtocolHandlers = new Protocol[type + 1];
			System.arraycopy(protocolHandlers, 0, newProtocolHandlers, 0, protocolHandlers.length);
			protocolHandlers = newProtocolHandlers;
			ServerHandler[] newServerHandlers = new ServerHandler[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			serverHandlers = newServerHandlers;
		}
		protocolHandlers[type] = customProtocol;
		serverHandlers[type] = customServerHandler;
	}
	
	public static Protocol getProtocol(int type){
		return protocolHandlers[type];
	}
	
	public static ServerHandler getServerHandler(int type){
		return serverHandlers[type];
	}
	
}
