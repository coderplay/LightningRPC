package info.minzhou.lightning.rpc.benchmark;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;

/**
 * ResponseObject Serializer
 * 
 */
public class ResponseObjectSerializer extends Serializer<ResponseObject> {

  private DefaultArraySerializers.ByteArraySerializer delegate = new DefaultArraySerializers.ByteArraySerializer();

  @Override
	public void write(Kryo kryo, Output output, ResponseObject resObject) {
    delegate.write(kryo, output, resObject.getBytes());
	}

  @Override
  public ResponseObject read(Kryo kryo, Input input,
      Class<ResponseObject> type) {
    byte[] bytes = delegate.read(kryo, input, byte[].class);
    return new ResponseObject(bytes);
  }
}
