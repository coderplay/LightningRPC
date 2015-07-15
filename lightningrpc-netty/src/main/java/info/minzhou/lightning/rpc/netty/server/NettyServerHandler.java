package info.minzhou.lightning.rpc.netty.server;


import info.minzhou.lightning.rpc.ProtocolFactory;
import info.minzhou.lightning.rpc.RequestWrapper;
import info.minzhou.lightning.rpc.ResponseWrapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty Server Handler
 *
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  private static final Log LOGGER = LogFactory.getLog(NettyServerHandler.class);

  private ExecutorService threadpool;

  public NettyServerHandler(ExecutorService threadpool) {
    this.threadpool = threadpool;
  }

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
      throws Exception {
    if (!(e.getCause() instanceof IOException)) {
      // only log
      LOGGER.error("catch some exception not IOException", e.getCause());
    }
  }

  public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
    if (!(msg instanceof RequestWrapper) && !(msg instanceof List)) {
      LOGGER.error("receive message error,only support RequestWrapper || List");
      throw new Exception(
          "receive message error,only support RequestWrapper || List");
    }
    handleRequest(ctx, msg);
  }

  @SuppressWarnings("unchecked")
  private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
    try {
      threadpool.execute(new HandlerRunnable(ctx, message, threadpool));
    } catch (RejectedExecutionException exception) {
      LOGGER.error("server threadpool full,threadpool maxsize is:"
          + ((ThreadPoolExecutor) threadpool).getMaximumPoolSize());
      if (message instanceof List) {
        List<RequestWrapper> requests = (List<RequestWrapper>) message;
        for (final RequestWrapper request : requests) {
          sendErrorResponse(ctx, request);
        }
      } else {
        sendErrorResponse(ctx, (RequestWrapper) message);
      }
    }
  }

  private void sendErrorResponse(final ChannelHandlerContext ctx, final RequestWrapper request) {
    ResponseWrapper responseWrapper =
        new ResponseWrapper(request.getId(), request.getCodecType(), request.getProtocolType());
    responseWrapper
        .setException(new Exception("server threadpool full,maybe because server is slow or too many requests"));
    ChannelFuture wf = ctx.channel().writeAndFlush(responseWrapper);
    wf.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          LOGGER.error("server write response error,request id is: " + request.getId());
        }
      }
    });
  }

  private static final ChannelFutureListener listener = new ChannelFutureListener() {
    public void operationComplete(ChannelFuture future) throws Exception {
      if (!future.isSuccess()) {
        LOGGER.error("server write response error");
      }
    }
  };

  class HandlerRunnable implements Runnable {

    private ChannelHandlerContext ctx;

    private Object message;

    private ExecutorService threadPool;


    public HandlerRunnable(ChannelHandlerContext ctx, Object message, ExecutorService threadPool) {
      this.ctx = ctx;
      this.message = message;
      this.threadPool = threadPool;
    }

    @SuppressWarnings("rawtypes")
    public void run() {
      // pipeline
      if (message instanceof List) {
        List messages = (List) message;
        for (Object messageObject : messages) {
          threadPool.execute(new HandlerRunnable(ctx, messageObject, threadPool));
        }
      } else {
        RequestWrapper request = (RequestWrapper) message;
        long beginTime = System.currentTimeMillis();
        ResponseWrapper responseWrapper =
            ProtocolFactory.getServerHandler(request.getProtocolType()).handleRequest(request);
        final int id = request.getId();
        // already timeout,so not return
        if ((System.currentTimeMillis() - beginTime) >= request.getTimeout()) {
          LOGGER.warn("timeout,so give up send response to client,requestId is:"
              + id
              + ",client is:"
              + ctx.channel().remoteAddress() + ",consumetime is:" + (System.currentTimeMillis() - beginTime) +
              ",timeout is:" + request.getTimeout());
          return;
        }
        ChannelFuture wf = ctx.writeAndFlush(responseWrapper);
        wf.addListener(listener);
      }
    }

  }

}
