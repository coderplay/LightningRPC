package code.google.nfs.rpc.netty4.server;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import io.netty.channel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * Netty4 Server Handler
 * 
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ServerHandler extends ChannelInboundHandlerAdapter {

	private static final Log LOGGER = LogFactory.getLog(Netty4ServerHandler.class);
	
	public Netty4ServerHandler(){
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
    EventLoop eventLoop = ctx.channel().eventLoop();

    // pipeline
    if(message instanceof List){
      List messages = (List) message;
      for (Object messageObject : messages) {
        eventLoop.execute(new HandlerRunnable(ctx, messageObject));
      }
    } else {
      eventLoop.execute(new HandlerRunnable(ctx, message));
    }
	}

  private static final ChannelFutureListener listener = new ChannelFutureListener() {
    public void operationComplete(ChannelFuture future) throws Exception {
      if(!future.isSuccess()){
        LOGGER.error("server write response error");
      }
    }
  };

  class HandlerRunnable implements Runnable{

		private ChannelHandlerContext ctx;
		
		private Object message;
		
    public HandlerRunnable(ChannelHandlerContext ctx,Object message){
			this.ctx = ctx;
			this.message = message;
		}
		
		@SuppressWarnings("rawtypes")
		public void run() {
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
      ChannelFuture wf = ctx.writeAndFlush(responseWrapper);
      wf.addListener(listener);
		}

	}
	
}
