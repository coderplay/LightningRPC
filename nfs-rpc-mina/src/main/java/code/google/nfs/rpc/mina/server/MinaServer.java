package code.google.nfs.rpc.mina.server;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.mina.serialize.MinaProtocolCodecFilter;
import code.google.nfs.rpc.server.Server;

/**
 * Mina Server
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaServer implements Server {

	private static final Log LOGGER = LogFactory.getLog(MinaServer.class);
	
	private static final String ACCEPTOR_THREADNAME = "MINASERVER-ACCEPTOR";

    private IoAcceptor acceptor;
    
    private AtomicBoolean startFlag = new AtomicBoolean();
    
    private MinaServerHandler serverHandler = null;
    
    public MinaServer(){
    	acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1,
                Executors.newCachedThreadPool(new NamedThreadFactory(ACCEPTOR_THREADNAME)));
        acceptor.getDefaultConfig().setThreadModel(ThreadModel.MANUAL);
        acceptor.getDefaultConfig().getFilterChain().addLast("objectserialize", new MinaProtocolCodecFilter());
    }
    
	public void start(int listenPort, ExecutorService businessThreadPool)
		throws Exception {
		if(!startFlag.compareAndSet(false, true)){
			return;
		}
        try{
        	serverHandler = new MinaServerHandler(businessThreadPool);
        	acceptor.bind(new InetSocketAddress(listenPort), serverHandler);
        	LOGGER.warn("Server started,listen at: "+listenPort);
        }
        catch(Exception e){
        	startFlag.set(false);
        	LOGGER.error("Server start failed",e);
        	throw new Exception("start server error",e);
        }
	}
	
	public void registerProcessor(int protocolType,String serviceName,Object serviceInstance){
		ProtocolFactory.getServerHandler(protocolType).registerProcessor(serviceName, serviceInstance);
	}

	public void stop() throws Exception {
		serverHandler = null;
		LOGGER.warn("Server stoped");
		acceptor.unbindAll();
		startFlag.set(false);
	}
}
