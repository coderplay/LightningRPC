package info.minzhou.lightning.rpc.protocol;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
/**
 * Java Encoder
 * 
 */
public class JavaEncoder implements Encoder {

	public byte[] encode(Object object) throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byteArray);
		output.writeObject(object);
		output.flush();
		output.close();
		return byteArray.toByteArray(); 
	}

}
