package code.google.nfs.rpc.grizzly.serialize;

/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import code.google.nfs.rpc.protocol.ProtocolUtils;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.attributes.Attribute;
import org.glassfish.grizzly.memory.CompositeBuffer;

/**
 * Grizzly Protocol Decoder
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyProtocolFilter extends BaseFilter {

    private static final Log LOGGER = LogFactory.getLog(GrizzlyProtocolFilter.class);
    private static final Attribute<CompositeBuffer> OUTPUT_BUFFER_ATTR =
            Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute(
            "GrizzlyProtocolFilter.outputBuffer");

    // decode object
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        final Object message = ctx.getMessage();
        if (message instanceof IncompleteBufferHolder) {
            return ctx.getStopAction(((IncompleteBufferHolder) message).buffer);
        }

        final Buffer buffer = (Buffer) message;

//        final int bufferLen = buffer.remaining();
        Object errorReturnObject = new Object();
        GrizzlyByteBufferWrapper wrapper = new GrizzlyByteBufferWrapper(buffer);

        try {
            final List<Object> list = new ArrayList<Object>();
            Object object;
            while ((object = ProtocolUtils.decode(wrapper, errorReturnObject))
                    != errorReturnObject) {
                list.add(object);
            }

            if (list.isEmpty()) {
                return ctx.getStopAction(buffer);
            } else {
                final Buffer remainder = buffer.hasRemaining()
                        ? buffer.split(buffer.position()) : null;
                buffer.dispose();

                ctx.setMessage(list);
                return ctx.getInvokeAction(new IncompleteBufferHolder(remainder));
            }
        } catch (Exception e) {
            LOGGER.error("decode message error", e);
            throw new IOException(e);
        }
    }

    // encode object
    public NextAction handleWrite(FilterChainContext ctx) throws IOException {
        GrizzlyByteBufferWrapper wrapper = new GrizzlyByteBufferWrapper(ctx);
        try {
            ProtocolUtils.encode(ctx.getMessage(), wrapper);
            final Buffer buffer = wrapper.getBuffer();
            buffer.trim();

            ctx.setMessage(buffer);
            return ctx.getInvokeAction();

        } catch (Exception e) {
            throw new IOException("encode message to byte error", e);
        }
    }

    private static class IncompleteBufferHolder {

        public IncompleteBufferHolder(Buffer buffer) {
            this.buffer = buffer;
        }
        private Buffer buffer;
    }
}
