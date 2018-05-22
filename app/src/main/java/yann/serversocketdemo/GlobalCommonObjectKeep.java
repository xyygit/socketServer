package yann.serversocketdemo;

import java.nio.ByteBuffer;

/**
 * Created by yayun.xia on 2018/5/22.
 */

public class GlobalCommonObjectKeep {
    /**
     * 全局MVB数据缓冲区 占用 1M 内存
     */
    private static ByteBuffer bbuf = ByteBuffer.allocate(40);

    private static byte[] buffer = new byte[40];

    /**
     * 线程安全的取得缓冲变量
     */
    public static synchronized ByteBuffer getByteBuffer() {
        return bbuf;
    }

    public static synchronized byte[] getBytes() {
        if (buffer == null) {
            buffer = new byte[40];
        }
        return buffer;
    }

    public static void clearBytes() {
        buffer = null;
    }

}
