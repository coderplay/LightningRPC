package code.google.nfs.rpc.grizzly.server;

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
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.EmptyCompletionHandler;

/**
 * Grizzly Server Handler
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyServerHandler extends BaseFilter {

    private static final Log LOGGER = LogFactory.getLog(GrizzlyServerHandler.class);

    private final ExecutorService threadPool;

    public GrizzlyServerHandler(final ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
    
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        final Object message = ctx.getMessage();
        final Connection connection = ctx.getConnection();

        try {
            threadPool.execute(new HandlerRunnable(connection, message, threadPool));
        } catch (RejectedExecutionException exception) {
            LOGGER.error("server threadpool full,threadpool maxsize is:"
                    + ((ThreadPoolExecutor) threadPool).getMaximumPoolSize());
            if (message instanceof List) {
                List<RequestWrapper> requests = (List<RequestWrapper>) message;
                for (final RequestWrapper request : requests) {
                    sendErrorResponse(connection, request);
                }
            } else {
                sendErrorResponse(connection, (RequestWrapper) message);
            }
        }

        return ctx.getStopAction();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendErrorResponse(final Connection connection, final RequestWrapper request) throws IOException {
        ResponseWrapper responseWrapper = new ResponseWrapper(request.getId(), request.getCodecType(), request.getProtocolType());
        responseWrapper.setException(new Exception("server threadpool full,maybe because server is slow or too many requests"));

        connection.write(responseWrapper, new EmptyCompletionHandler<WriteResult>() {

            @Override
            public void failed(Throwable throwable) {
                  LOGGER.error("server write response error,request id is: " + request.getId());
            }
            
        });
    }

    class HandlerRunnable implements Runnable {

        private final Connection connection;
        private final Object message;
        private final ExecutorService threadPool;

        public HandlerRunnable(Connection connection,
                Object message,
                ExecutorService threadPool) {
            this.connection = connection;
            this.message = message;
            this.threadPool = threadPool;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void run() {
            // pipeline
            if (message instanceof List) {
                List messages = (List) message;
                for (Object messageObject : messages) {
                    threadPool.execute(new HandlerRunnable(connection, messageObject, threadPool));
                }
            } else {
                RequestWrapper request = (RequestWrapper) message;
                long beginTime = System.currentTimeMillis();
                ResponseWrapper responseWrapper = ProtocolFactory.getServerHandler(request.getProtocolType()).handleRequest(request);
                final int id = request.getId();
                // already timeout,so not return
                if ((System.currentTimeMillis() - beginTime) >= request.getTimeout()) {
                    LOGGER.warn("timeout,so give up send response to client,requestId is:"
                            + id
                            + ",client is:"
                            + connection.getPeerAddress() + ",consumetime is:" + (System.currentTimeMillis() - beginTime) + ",timeout is:" + request.getTimeout());
                    return;
                }

                connection.write(responseWrapper, new EmptyCompletionHandler<WriteResult>() {
                    @Override
                    public void failed(Throwable throwable) {
                        LOGGER.error("server write response error,request id is: " + id);
                    }
                });
            }
        }
    }
    
}
