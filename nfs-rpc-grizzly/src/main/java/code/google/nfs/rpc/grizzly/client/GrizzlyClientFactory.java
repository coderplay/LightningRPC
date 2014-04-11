package code.google.nfs.rpc.grizzly.client;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.grizzly.serialize.GrizzlyProtocolFilter;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

/**
 * Grizzly Client Factory
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyClientFactory extends AbstractClientFactory {

    private static final ClientFactory _self = new GrizzlyClientFactory();

    private GrizzlyClientFactory() {
        ;
    }

    public static ClientFactory getInstance() {
        return _self;
    }

    @SuppressWarnings("rawtypes")
    protected Client createClient(String targetIP, int targetPort, int connectTimeout, String key)
            throws Exception {
        Connection connection = null;
        GrizzlyClientHandler handler = new GrizzlyClientHandler();
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new GrizzlyProtocolFilter());
        filterChainBuilder.add(handler);


        final TCPNIOTransportBuilder transportBuilder = TCPNIOTransportBuilder.newInstance();
        transportBuilder.setOptimizedForMultiplexing(true);

        transportBuilder.setIOStrategy(SameThreadIOStrategy.getInstance());
        
        final TCPNIOTransport transport = transportBuilder.build();
        transport.setTcpNoDelay(Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));

        transport.setProcessor(filterChainBuilder.build());
                
        transport.start();
        Future<Connection> future = transport.connect(targetIP, targetPort);
        if (connectTimeout < 1000) {
            connectTimeout = 1000;
        }
        connection = future.get(connectTimeout, TimeUnit.MILLISECONDS);
        @SuppressWarnings("unchecked")
        GrizzlyClient client = new GrizzlyClient(targetIP, targetPort, connectTimeout, connection, key);
        handler.setClient(client);
        return client;
    }    
}
