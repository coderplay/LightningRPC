package info.minzhou.lightning.rpc.netty.client;

/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import info.minzhou.lightning.rpc.RequestWrapper;
import info.minzhou.lightning.rpc.ResponseWrapper;
import info.minzhou.lightning.rpc.client.AbstractClient;
import info.minzhou.lightning.rpc.client.Client;
import info.minzhou.lightning.rpc.client.ClientFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;

/**
 * Netty Client
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class NettyClient extends AbstractClient {

  private static final Log LOGGER = LogFactory.getLog(NettyClient.class);

  private ChannelFuture cf;

  private String key;

  private int connectTimeout;

  public NettyClient(ChannelFuture cf, String key, int connectTimeout) {
    this.cf = cf;
    this.key = key;
    this.connectTimeout = connectTimeout;
  }

  public void sendRequest(final RequestWrapper wrapper, final int timeout)
      throws Exception {
    final long beginTime = System.currentTimeMillis();
    final Client self = this;
    ChannelFuture writeFuture = cf.channel().writeAndFlush(wrapper);
    // use listener to avoid wait for write & thread context switch
    writeFuture.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future)
          throws Exception {
        if (future.isSuccess()) {
          return;
        }
        String errorMsg = "";
        // write timeout
        if (System.currentTimeMillis() - beginTime >= timeout) {
          errorMsg = "write to send buffer consume too long time("
              + (System.currentTimeMillis() - beginTime)
              + "),request id is:" + wrapper.getId();
        }
        if (future.isCancelled()) {
          errorMsg = "Send request to " + cf.channel().toString()
              + " cancelled by user,request id is:"
              + wrapper.getId();
        }
        if (!future.isSuccess()) {
          if (cf.channel().isOpen()) {
            // maybe some exception,so close the channel
            cf.channel().close();
          } else {
            NettyClientFactory.getInstance().removeClient(key, self);
          }
          errorMsg = "Send request to " + cf.channel().toString() + " error" + future.cause();
        }
        LOGGER.error(errorMsg);
        ResponseWrapper response =
            new ResponseWrapper(wrapper.getId(), wrapper.getCodecType(), wrapper.getProtocolType());
        response.setException(new Exception(errorMsg));
        self.putResponse(response);
      }
    });
  }

  public String getServerIP() {
    return ((InetSocketAddress) cf.channel().remoteAddress()).getHostName();
  }

  public int getServerPort() {
    return ((InetSocketAddress) cf.channel().remoteAddress()).getPort();
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public long getSendingBytesSize() {
    // TODO: implement it
    return 0;
  }

  public ClientFactory getClientFactory() {
    return NettyClientFactory.getInstance();
  }

}
