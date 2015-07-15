package info.minzhou.lightning.rpc;

import info.minzhou.lightning.rpc.protocol.Decoder;
import info.minzhou.lightning.rpc.protocol.Encoder;
import info.minzhou.lightning.rpc.protocol.JavaDecoder;
import info.minzhou.lightning.rpc.protocol.JavaEncoder;
import info.minzhou.lightning.rpc.protocol.KryoDecoder;
import info.minzhou.lightning.rpc.protocol.KryoEncoder;
import info.minzhou.lightning.rpc.protocol.PBDecoder;
import info.minzhou.lightning.rpc.protocol.PBEncoder;

/**
 * Encoder and Decoder Register
 * 
 */
public class Codecs {
	
	public static final int JAVA_CODEC = 1;
	
	public static final int HESSIAN_CODEC = 2;
	
	public static final int PB_CODEC = 3;
	
	public static final int KRYO_CODEC = 4;
	
	private static Encoder[] encoders = new Encoder[5];
	
	private static Decoder[] decoders = new Decoder[5];
	
	static{
		addEncoder(JAVA_CODEC, new JavaEncoder());
		addEncoder(PB_CODEC, new PBEncoder());
		addEncoder(KRYO_CODEC, new KryoEncoder());
		addDecoder(JAVA_CODEC, new JavaDecoder());
		addDecoder(PB_CODEC, new PBDecoder());
		addDecoder(KRYO_CODEC, new KryoDecoder());
	}
	
	public static void addEncoder(int encoderKey,Encoder encoder){
		if(encoderKey > encoders.length){
			Encoder[] newEncoders = new Encoder[encoderKey + 1];
			System.arraycopy(encoders, 0, newEncoders, 0, encoders.length);
			encoders = newEncoders;
		}
		encoders[encoderKey] = encoder;
	}
	
	public static void addDecoder(int decoderKey,Decoder decoder){
		if(decoderKey > decoders.length){
			Decoder[] newDecoders = new Decoder[decoderKey + 1];
			System.arraycopy(decoders, 0, newDecoders, 0, decoders.length);
			decoders = newDecoders;
		}
		decoders[decoderKey] = decoder;
	}
	
	public static Encoder getEncoder(int encoderKey){
		return encoders[encoderKey];
	}
	
	public static Decoder getDecoder(int decoderKey){
		return decoders[decoderKey];
	}
	
}
