package code.google.nfs.rpc.netty4.client;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolDecoder;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * Netty Client Factory,to create client based on netty4 API
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ClientFactory extends AbstractClientFactory {

  private static final Log LOGGER = LogFactory.getLog(Netty4ClientFactory.class);

  private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

  private static AbstractClientFactory _self = new Netty4ClientFactory();

  private static final ThreadFactory workerThreadFactory = new NamedThreadFactory("NETTYCLIENT-WORKER-");

  private static EventLoopGroup workerGroup = new EpollEventLoopGroup(PROCESSORS, workerThreadFactory);

  private Netty4ClientFactory() {
    ;
  }

  public static AbstractClientFactory getInstance() {
    return _self;
  }

  protected Client createClient(String targetIP, int targetPort,
                                int connectTimeout, String key) throws Exception {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(workerGroup)
        .channel(EpollSocketChannel.class)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")))
        .option(ChannelOption.SO_REUSEADDR,
            Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")));
    if (connectTimeout < 1000) {
      bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
    } else {
      bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
    }
    final Netty4ClientHandler handler = new Netty4ClientHandler(this, key);
    bootstrap.handler(new ChannelInitializer<SocketChannel>() {

      protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new Netty4ProtocolDecoder());
        pipeline.addLast("encoder", new Netty4ProtocolEncoder());
        pipeline.addLast("handler", handler);
      }

    });
    ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort)).sync();
    future.awaitUninterruptibly(connectTimeout);
    if (!future.isDone()) {
      LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " timeout!");
      throw new Exception("Create connection to " + targetIP + ":" + targetPort + " timeout!");
    }
    if (future.isCancelled()) {
      LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
      throw new Exception("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
    }
    if (!future.isSuccess()) {
      LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " error", future.cause());
      throw new Exception("Create connection to " + targetIP + ":" + targetPort + " error", future.cause());
    }
    Netty4Client client = new Netty4Client(future, key, connectTimeout);
    handler.setClient(client);
    return client;
  }

}
