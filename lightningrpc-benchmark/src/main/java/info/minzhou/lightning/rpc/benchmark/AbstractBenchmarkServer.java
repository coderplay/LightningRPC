package info.minzhou.lightning.rpc.benchmark;

import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.google.protobuf.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import info.minzhou.lightning.rpc.NamedThreadFactory;
import info.minzhou.lightning.rpc.protocol.KryoUtils;
import info.minzhou.lightning.rpc.protocol.PBDecoder;
import info.minzhou.lightning.rpc.protocol.RPCProtocol;
import info.minzhou.lightning.rpc.protocol.SimpleProcessorProtocol;
import info.minzhou.lightning.rpc.server.Server;
import info.minzhou.lightning.rpc.server.ServerProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Abstract benchmark server
 * 
 * Usage: BenchmarkServer listenPort maxThreads responseSize
 * 
 */
public abstract class AbstractBenchmarkServer {

	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

  public void run(String[] args) throws Exception {
    Config conf = ConfigFactory.load();
    int listenPort = conf.getInt("server.port");
    int maxThreads = conf.getInt("server.threads");
    final int responseSize = conf.getInt("server.response.size");
    System.out.println(dateFormat.format(new Date())
        + " ready to start server,listenPort is: " + listenPort
        + ",maxThreads is:" + maxThreads + ",responseSize is:"
        + responseSize + " bytes");

    Server server = getServer();
    server.registerProcessor(SimpleProcessorProtocol.TYPE,RequestObject.class.getName(), new ServerProcessor() {
      public Object handle(Object request) throws Exception {
        return new ResponseObject(responseSize);
      }
    });
    // for pb codec
    PBDecoder.addMessage(PB.RequestObject.class.getName(), PB.RequestObject.getDefaultInstance());
    PBDecoder.addMessage(PB.ResponseObject.class.getName(), PB.ResponseObject.getDefaultInstance());
    server.registerProcessor(SimpleProcessorProtocol.TYPE,PB.RequestObject.class.getName(), new ServerProcessor() {
      public Object handle(Object request) throws Exception {
        PB.ResponseObject.Builder  builder = PB.ResponseObject.newBuilder();
        builder.setBytesObject(ByteString.copyFrom(new byte[responseSize]));
        return builder.build();
      }
    });
    server.registerProcessor(RPCProtocol.TYPE, "testservice", new BenchmarkTestServiceImpl(responseSize));
    server.registerProcessor(RPCProtocol.TYPE, "testservicepb", new PBBenchmarkTestServiceImpl(responseSize));
    KryoUtils.registerClass(byte[].class, new DefaultArraySerializers.ByteArraySerializer(), 0);
    KryoUtils.registerClass(RequestObject.class, new RequestObjectSerializer(), 1);
    KryoUtils.registerClass(ResponseObject.class, new ResponseObjectSerializer(), 2);

    ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
    ExecutorService threadPool = new ThreadPoolExecutor(20, maxThreads,
        300, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), tf);
    server.start(listenPort, threadPool);
  }

  /**
	 * Get server instance
	 */
	public abstract Server getServer();

}
