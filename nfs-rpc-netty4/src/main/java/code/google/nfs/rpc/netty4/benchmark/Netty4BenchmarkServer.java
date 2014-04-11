package code.google.nfs.rpc.netty4.benchmark;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import code.google.nfs.rpc.benchmark.*;
import code.google.nfs.rpc.netty4.server.Netty4Server;
import code.google.nfs.rpc.protocol.PBDecoder;
import code.google.nfs.rpc.protocol.RPCProtocol;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;
import code.google.nfs.rpc.server.Server;
import code.google.nfs.rpc.server.ServerProcessor;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.google.protobuf.ByteString;

import java.util.Date;

/**
 * Netty RPC Benchmark Server
 * 
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4BenchmarkServer extends AbstractBenchmarkServer {

	public static void main(String[] args) throws Exception{
		new Netty4BenchmarkServer().run(args);
	}

  public Server getServer() {
		return new Netty4Server();
	}

}
