package info.minzhou.lightning.rpc.netty.server;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.NamedThreadFactory;
import info.minzhou.lightning.rpc.ProtocolFactory;
import info.minzhou.lightning.rpc.netty.serialize.NettyProtocolDecoder;
import info.minzhou.lightning.rpc.netty.serialize.NettyProtocolEncoder;
import info.minzhou.lightning.rpc.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty Server
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettyServer implements Server {

  private static final Log LOGGER = LogFactory.getLog(NettyServer.class);

  private ServerBootstrap bootstrap = null;

  private AtomicBoolean startFlag = new AtomicBoolean(false);

  private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

  public NettyServer() {
    ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
    ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
    EventLoopGroup bossGroup = new EpollEventLoopGroup(PROCESSORS, serverBossTF);
    EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(PROCESSORS * 2, serverWorkerTF);
    workerGroup.setIoRatio(Integer.parseInt(System.getProperty("nfs.rpc.io.ratio", "80")));
    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup)
        .channel(EpollServerSocketChannel.class)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.SO_REUSEADDR,
            Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")))
        .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
  }

  public void start(int listenPort, final ExecutorService threadPool) throws Exception {
    if (!startFlag.compareAndSet(false, true)) {
      return;
    }
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

      protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new NettyProtocolDecoder());
        pipeline.addLast("encoder", new NettyProtocolEncoder());
        pipeline.addLast("handler", new NettyServerHandler(threadPool));
      }

    });
    bootstrap.bind(new InetSocketAddress(listenPort)).sync();
    LOGGER.warn("Server started,listen at: " + listenPort);
  }

  public void registerProcessor(int protocolType, String serviceName, Object serviceInstance) {
    ProtocolFactory.getServerHandler(protocolType).registerProcessor(serviceName, serviceInstance);
  }

  public void stop() throws Exception {
    LOGGER.warn("Server stop!");
    startFlag.set(false);
  }

}
