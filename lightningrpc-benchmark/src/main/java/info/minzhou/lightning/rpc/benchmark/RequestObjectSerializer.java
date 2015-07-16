package info.minzhou.lightning.rpc.benchmark;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;

/**
 * RequestObject Serializer
 * 
 */
public class RequestObjectSerializer extends Serializer<RequestObject> {
  private DefaultArraySerializers.ByteArraySerializer delegate = new DefaultArraySerializers.ByteArraySerializer();

  @Override
  public void write(Kryo kryo, Output output, RequestObject reqObject) {
    delegate.write(kryo, output, reqObject.getBytes());
  }

  @Override
  public RequestObject read(Kryo kryo, Input input, Class<RequestObject> type) {
    byte[] bytes = delegate.read(kryo, input, byte[].class);
    return new RequestObject(bytes);
  }
}
