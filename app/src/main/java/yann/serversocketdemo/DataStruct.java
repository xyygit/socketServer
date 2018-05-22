package yann.serversocketdemo;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yayun.xia on 2018/5/21.
 */

public class DataStruct {

    public final static int BUFFER_CAPACITY = 4128;//缓冲区容量

    public int length;   // 长度（2byte）
    public int reserve16;// 保留，不使用（2byte）
    public int reserve8; // 保留，不使用（1byte）
    public int gain;     // 增益（1byte）
    public int adSampleDiv;// 采样（1byte）
    public int battery;  // 电池（1byte）
    public int x_position;//（4byte）
    public int y_position;//（4byte）
    public List<Integer> data = new ArrayList<>();

    private byte[] headFlag = {0x0D, 0x0A, 0x55, (byte) 0xAA, (byte) 0xA5, 0x5A, 0x0D, 0x0A};
    private byte[] endFlag = {0x0D, 0x0A, (byte) 0xA5, 0x5A, (byte) 0xB6, 0x6B, 0x0D, 0x0A};


    /**
     * 全局MVB数据缓冲区 占用 1M 内存
     */
    private static ByteBuffer bbuf = ByteBuffer.allocate(BUFFER_CAPACITY);

    /**
     * 临时接收字节数组
     */
    private static byte[] tempBytes = new byte[BUFFER_CAPACITY];

    /**
     * 线程安全的取得缓冲变量
     */
    public static synchronized ByteBuffer getByteBuffer() {
        return bbuf;
    }

    /**
     * 线程安全的取得临时接收变量
     */
    public static synchronized byte[] getTempBytes() {
        if (tempBytes == null) {
            tempBytes = new byte[BUFFER_CAPACITY];
        }
        return tempBytes;
    }

    /**
     * 每次接收客户端数据时临时数组要重置
     */
    public static void clearTempBytes() {
        tempBytes = null;
    }

    public DataStruct() {
    }

    public void parseData(byte[] buffer) {

        byte[] data = TypeUtil.subBytes(buffer, 8, BUFFER_CAPACITY - 16);

        this.length = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 0, 2));
        this.reserve16 = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 2, 2));
        this.reserve8 = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 4, 1));
        this.gain = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 5, 1));
        this.adSampleDiv = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 6, 1));
        this.battery = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 7, 1));
        this.x_position = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 8, 4));
        this.y_position = TypeUtil.bytes2Int(TypeUtil.subBytes(data, 12, 4));

        this.data = TypeUtil.bytes2data(TypeUtil.subBytes(data, 16, length - 16));

        Log.d("解析完成", toString());
    }

    public boolean isFullPackage(byte[] buffer) {
        if (buffer != null && buffer.length == BUFFER_CAPACITY) {
            if (isHeadFlag(buffer) && isEndFlag(buffer)) {
                return true;
            }
        }
        Log.e("解析错误", "这不是一个正确的完整包");
        return false;
    }

    /**
     * 组包解析
     *
     * @param recBytes 接收数据
     */
    public void parsePackage(byte[] recBytes) {
        try {
            ByteBuffer byteBuffer = DataStruct.getByteBuffer();
            if (isFullPackage(recBytes)) {
                //todo 完整的包直接解析
                parseData(recBytes);
                byteBuffer.clear();
                return;
            } else if (recBytes.length <= byteBuffer.remaining()) {
                if (isHeadFlag(recBytes)) {
                    byteBuffer.clear();
                    byteBuffer.put(recBytes);
                    return;
                } else if (isEndFlag(recBytes)) {
                    byteBuffer.put(recBytes);
                    if (byteBuffer.remaining() == 0) {
                        byte[] bytes = new byte[BUFFER_CAPACITY];
                        byteBuffer.flip();
                        byteBuffer.get(bytes);
                        if (isFullPackage(bytes)) {
                            parseData(bytes);
                            byteBuffer.clear();
                        }
                    }

                } else {
                    byteBuffer.put(recBytes);
                }
            }
            Log.d("此次组包结束", "byteBuffer" + byteBuffer + " / remain:" + byteBuffer.remaining());
        } catch (Exception e) {
            Log.e("组包结束", "组包异常");
            e.printStackTrace();
        }


    }

    @Override
    public String toString() {
        return "DataStruct{" +
                "length=" + length +
                ", reserve16=" + reserve16 +
                ", reserve8=" + reserve8 +
                ", gain=" + gain +
                ", adSampleDiv=" + adSampleDiv +
                ", battery=" + battery +
                ", x_position=" + x_position +
                ", y_position=" + y_position +
                ", data=" + data +
                '}';
    }

    public boolean isHeadFlag(byte[] buffer) {
        if (buffer != null && buffer.length >= 8) {
            return Arrays.equals(TypeUtil.subBytes(buffer, 0, 8), headFlag);
        }
        return false;
    }

    public boolean isEndFlag(byte[] buffer) {
        if (buffer != null && buffer.length >= 8) {
            return Arrays.equals(TypeUtil.subBytes(buffer, buffer.length - 8, 8), endFlag);
        }
        return Arrays.equals(buffer, endFlag);
    }

}
