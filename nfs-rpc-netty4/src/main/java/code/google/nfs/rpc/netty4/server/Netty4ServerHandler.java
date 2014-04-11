package code.google.nfs.rpc.netty4.server;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
/**
 * Netty4 Server Handler
 * 
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ServerHandler extends ChannelInboundHandlerAdapter {

	private static final Log LOGGER = LogFactory.getLog(Netty4ServerHandler.class);
	
	private ExecutorService threadpool;
	
	public Netty4ServerHandler(ExecutorService threadpool){
		this.threadpool = threadpool;
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
		throws Exception {
		if(!(e.getCause() instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",e.getCause());
		}
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
		throws Exception {
		if (!(msg instanceof RequestWrapper) && !(msg instanceof List)) {
			LOGGER.error("receive message error,only support RequestWrapper || List");
			throw new Exception(
					"receive message error,only support RequestWrapper || List");
		}
		handleRequest(ctx,msg);
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
		ChannelFuture wf = ctx.channel().writeAndFlush(responseWrapper);
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
							+ ctx.channel().remoteAddress()+",consumetime is:"+(System.currentTimeMillis() - beginTime)+",timeout is:"+request.getTimeout());
					return;
				}
				ChannelFuture wf = ctx.channel().writeAndFlush(responseWrapper);
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
