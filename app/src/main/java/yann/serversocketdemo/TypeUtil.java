package yann.serversocketdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yayun.xia on 2018/5/21.
 */

public class TypeUtil {

    public static int byte2Int(byte b) {
        return b & 0xFF;
    }

    public static int bytes2Int(byte[] bs) {
        if (bs.length == 4) {
            // 如果确定足4位，可直接返回值
            return (bs[0] & 0xFF) | ((bs[1] & 0xFF) << 8) | ((bs[2] & 0xFF) << 16) | ((bs[3] & 0xFF) << 24);
        }

        int retVal = 0;
        int len = bs.length < 4 ? bs.length : 4;
        for (int i = 0; i < len; i++) {
            retVal |= (bs[i] & 0xFF) << ((i & 0x03) << 3);
        }
        return retVal;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        String str = "--->";
        for (int i = 0; i < bs.length; i++) {
            str += bs[i] + " ";
        }
//        System.out.println("===subBytes==[" + str + "]");
        return bs;
    }

    public static List<Integer> bytes2data(byte[] bs) {
        List<Integer> list = new ArrayList<>();
        if (bs != null && bs.length > 0) {
            if (bs.length % 2 == 1) {
                bs = Arrays.copyOf(bs, bs.length + 1);
            }
            String s = " ";
            for (int i = 0; i < bs.length; i = i + 2) {
                int o = bytes2Int(subBytes(bs, i, 2));
                s += o + " ";
                list.add(o);
            }

//            System.out.println("转换后的数据：[" + s + "]");

        }
        return list;
    }
}
