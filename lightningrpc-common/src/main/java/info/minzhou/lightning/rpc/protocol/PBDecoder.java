
package info.minzhou.lightning.rpc.protocol;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Message;

/**
 * ProtocolBuf Decoder
 * 
 */
public class PBDecoder implements Decoder {
	
	private static ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<String, Message>();

	public static void addMessage(String className,Message message){
		messages.putIfAbsent(className, message);
	}
	
	public Object decode(String className,byte[] bytes) throws Exception {
		Message message = messages.get(className);
		return message.newBuilderForType().mergeFrom(bytes).build();
	}

}
