package code.google.nfs.rpc.netty4.server;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
/**
 * Netty Server Handler
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettyServerHandler extends SimpleChannelUpstreamHandler {

	private static final Log LOGGER = LogFactory.getLog(NettyServerHandler.class);
	
	private ExecutorService threadpool;
	
	public NettyServerHandler(ExecutorService threadpool){
		this.threadpool = threadpool;
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
		throws Exception {
		if(!(e.getCause() instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",e.getCause());
		}
	}
	
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e)
		throws Exception {
		Object message = e.getMessage();
		if (!(message instanceof RequestWrapper) && !(message instanceof List)) {
			LOGGER.error("receive message error,only support RequestWrapper || List");
			throw new Exception(
					"receive message error,only support RequestWrapper || List");
		}
		handleRequest(ctx,message);
	}
	
	@SuppressWarnings("unchecked")
	private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
		try {
			threadpool.execute(new HandlerRunnable(ctx, message, threadpool));
		} 
		catch (RejectedExecutionException exception) {
			LOGGER.error("server threadpool full,threadpool maxsize is:"
					+ ((ThreadPoolExecutor) threadpool).getMaximumPoolSize());
			if(message instanceof List){
				List<RequestWrapper> requests = (List<RequestWrapper>) message;
				for (final RequestWrapper request : requests) {
					sendErrorResponse(ctx, request);
				}
			}
			else{
				sendErrorResponse(ctx, (RequestWrapper) message);
			}
		}
	}

	private void sendErrorResponse(final ChannelHandlerContext ctx,final RequestWrapper request) {
		ResponseWrapper responseWrapper = new ResponseWrapper(request.getId(),request.getCodecType(),request.getProtocolType());
		responseWrapper
				.setException(new Exception("server threadpool full,maybe because server is slow or too many requests"));
		ChannelFuture wf = ctx.getChannel().write(responseWrapper);
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()){
					LOGGER.error("server write response error,request id is: "+request.getId());
				}
			}
		});
	}
	
	class HandlerRunnable implements Runnable{

		private ChannelHandlerContext ctx;
		
		private Object message;
		
		private ExecutorService threadPool;
		
		public HandlerRunnable(ChannelHandlerContext ctx,Object message,ExecutorService threadPool){
			this.ctx = ctx;
			this.message = message;
			this.threadPool = threadPool;
		}
		
		@SuppressWarnings("rawtypes")
		public void run() {
			// pipeline
			if(message instanceof List){
				List messages = (List) message;
				for (Object messageObject : messages) {
					threadPool.execute(new HandlerRunnable(ctx, messageObject, threadPool));
				}
			}
			else{
				RequestWrapper request = (RequestWrapper)message;
				long beginTime = System.currentTimeMillis();
				ResponseWrapper responseWrapper = ProtocolFactory.getServerHandler(request.getProtocolType()).handleRequest(request);
				final int id = request.getId();
				// already timeout,so not return
				if ((System.currentTimeMillis() - beginTime) >= request.getTimeout()) {
					LOGGER.warn("timeout,so give up send response to client,requestId is:"
							+ id
							+ ",client is:"
							+ ctx.getChannel().getRemoteAddress()+",consumetime is:"+(System.currentTimeMillis() - beginTime)+",timeout is:"+request.getTimeout());
					return;
				}
				ChannelFuture wf = ctx.getChannel().write(responseWrapper);
				wf.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if(!future.isSuccess()){
							LOGGER.error("server write response error,request id is: " + id);
						}
					}
				});
			}
		}
		
	}
	
}
