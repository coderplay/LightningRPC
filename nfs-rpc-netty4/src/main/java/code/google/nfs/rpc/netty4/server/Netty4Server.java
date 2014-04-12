package code.google.nfs.rpc.netty4.server;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolDecoder;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolEncoder;
import code.google.nfs.rpc.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
public class Netty4Server implements Server {

	private static final Log LOGGER = LogFactory.getLog(Netty4Server.class);
	
	private ServerBootstrap bootstrap = null;

	private AtomicBoolean startFlag = new AtomicBoolean(false);
	
	private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
	
	public Netty4Server(int workerThreads) {
		ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
		ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
		EventLoopGroup bossGroup = new EpollEventLoopGroup(PROCESSORS, serverBossTF);
    EpollEventLoopGroup workerGroup =
        new EpollEventLoopGroup(workerThreads > 0 ? workerThreads : PROCESSORS * 2, serverWorkerTF);
    workerGroup.setIoRatio(Integer.parseInt(System.getProperty("nfs.rpc.io.ratio", "30")));
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup,workerGroup)
			     .channel(EpollServerSocketChannel.class)
			     .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
           .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			     .option(ChannelOption.SO_REUSEADDR, Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")))
			     .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
	}

	public void start(int listenPort, final ExecutorService ignore) throws Exception {
		if(!startFlag.compareAndSet(false, true)){
			return;
		}
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			protected void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("decoder", new Netty4ProtocolDecoder());
				pipeline.addLast("encoder", new Netty4ProtocolEncoder());
				pipeline.addLast("handler", new Netty4ServerHandler());
			}
			
		});
		bootstrap.bind(new InetSocketAddress(listenPort)).sync();
		LOGGER.warn("Server started,listen at: "+listenPort);
	}

	public void registerProcessor(int protocolType,String serviceName, Object serviceInstance) {
		ProtocolFactory.getServerHandler(protocolType).registerProcessor(serviceName, serviceInstance);
	}
	
	public void stop() throws Exception {
		LOGGER.warn("Server stop!");
		startFlag.set(false);
	}

}
