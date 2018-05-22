package yann.lib;

import java.nio.ByteBuffer;

public class myClass {
    public static void main(String args[]){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        System.out.println("buffer = " + buffer);
        buffer.put(new byte[]{0x01,0x02});
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());

        buffer.put(new byte[]{0x03});
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());

        buffer.put(new byte[]{0x04});
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());

        buffer.flip();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());
        System.out.println("buffer = " + buffer+ " / get:"+bytes.length+" " + bytes[0]+" " +bytes[1]);

        buffer.clear();
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());
        buffer.put(new byte[]{0x06,0x07,0x08});
        System.out.println("buffer = " + buffer+ " / remain:"+buffer.remaining());
    }

}
