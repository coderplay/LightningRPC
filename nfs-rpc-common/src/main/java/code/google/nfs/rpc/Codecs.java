package code.google.nfs.rpc;
/**
 * nfs-rpc
 *   Apache License
 *   
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */
import code.google.nfs.rpc.protocol.Decoder;
import code.google.nfs.rpc.protocol.Encoder;
import code.google.nfs.rpc.protocol.HessianDecoder;
import code.google.nfs.rpc.protocol.HessianEncoder;
import code.google.nfs.rpc.protocol.JavaDecoder;
import code.google.nfs.rpc.protocol.JavaEncoder;
import code.google.nfs.rpc.protocol.KryoDecoder;
import code.google.nfs.rpc.protocol.KryoEncoder;
import code.google.nfs.rpc.protocol.PBDecoder;
import code.google.nfs.rpc.protocol.PBEncoder;
/**
 * Encoder & Decoder Register
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
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
		addEncoder(HESSIAN_CODEC, new HessianEncoder());
		addEncoder(PB_CODEC, new PBEncoder());
		addEncoder(KRYO_CODEC, new KryoEncoder());
		addDecoder(JAVA_CODEC, new JavaDecoder());
		addDecoder(HESSIAN_CODEC, new HessianDecoder());
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
