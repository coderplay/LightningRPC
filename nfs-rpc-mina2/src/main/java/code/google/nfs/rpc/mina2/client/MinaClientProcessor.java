package code.google.nfs.rpc.mina2.client;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import code.google.nfs.rpc.ResponseWrapper;
/**
 * Mina Client processor for receive message,handle exception
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClientProcessor extends IoHandlerAdapter {
	
	private static final Log LOGGER = LogFactory.getLog(MinaClientProcessor.class);
	
	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	private MinaClient client=null;
	
	private MinaClientFactory factory=null;
	
	private String key=null;
	
	public MinaClientProcessor(MinaClientFactory factory,String key){
		this.factory = factory;
		this.key = key;
	}
	
	public void setClient(MinaClient minaClient){
		this.client = minaClient;
	}
	
	public void messageReceived(IoSession session, Object message) throws Exception {
		if(message instanceof List){
			@SuppressWarnings("unchecked")
			List<ResponseWrapper> responses = (List<ResponseWrapper>)message;
			if(isDebugEnabled){
				// for performance trace
				LOGGER.debug("receive response list from server: "+session.getRemoteAddress()+",list size is:"+responses.size());
			}
			client.putResponses(responses);
		}
		else if(message instanceof ResponseWrapper){
			ResponseWrapper response = (ResponseWrapper)message;
			if(isDebugEnabled){
				// for performance trace
				LOGGER.debug("receive response list from server: "+session.getRemoteAddress()+",request is:"+response.getRequestId());
			}
			client.putResponse(response);
		}
		else{
			LOGGER.error("receive message error,only support List || ResponseWrapper");
			throw new Exception("receive message error,only support List || ResponseWrapper");
		}
	}
	
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		if(!(cause instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",cause);
		}
	}

	public void sessionClosed(IoSession session) throws Exception {
		LOGGER.warn("session closed,server is: "+session.getRemoteAddress());
		factory.removeClient(key,client);
	}
	
}
