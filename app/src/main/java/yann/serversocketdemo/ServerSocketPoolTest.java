package yann.serversocketdemo;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yayun.xia on 2018/5/21.
 */

public class ServerSocketPoolTest {

    public MainActivity.MyHandler handler;

    public ServerSocketPoolTest() {
    }

    public void testCommon(MainActivity.MyHandler myHandler) {
        this.handler = myHandler;
        ServerSocket serverSocket = null;
        //定义一个容量为50的线程
        ExecutorService service = Executors.newFixedThreadPool(50);
        try {
            serverSocket = new ServerSocket(6666);
            while (true) {
                System.out.println("wait receive message from client...");
                //接收客户端连接的socket对象
                Socket connection = null;
                //接收客户端传过来的数据，会阻塞
                connection = serverSocket.accept();
                service.submit(new SubPolThread(connection, myHandler));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class SubPolThread implements Callable<Boolean> {
    private Socket connection;
    private MainActivity.MyHandler myHandler;

    public SubPolThread(Socket conSocket) {
        this.connection = conSocket;
    }

    public SubPolThread(Socket connection, MainActivity.MyHandler myHandler) {
        this.connection = connection;
        this.myHandler = myHandler;
    }

    /**
     * 读取客户端信息
     *
     * @param inputStream
     */
    private void readMessageFromClient(InputStream inputStream) throws IOException {
        int realSize;
        DataStruct dataStruct = new DataStruct();
        while ((realSize = inputStream.read(DataStruct.getTempBytes())) != -1) {
            System.out.println("rendMessage:temp " + realSize + " / buffer: " + DataStruct.getTempBytes());
            String str = "buffer[] = [";
            byte[] realBytes = new byte[realSize];
            System.arraycopy(DataStruct.getTempBytes(), 0, realBytes, 0, realSize);
            for (int i = 0; i < realBytes.length; i++) {
                if (i == realBytes.length - 1) {
                    str += TypeUtil.byte2Int(DataStruct.getTempBytes()[i]) + "]";
                } else {
                    str += TypeUtil.byte2Int(DataStruct.getTempBytes()[i]) + " ";
                }
            }
            Log.d("接收的数据","" + str);

            dataStruct.parsePackage(realBytes);

            DataStruct.clearTempBytes();

        }
    }

    /**
     * 响应客户端信息
     *
     * @param outputStream
     * @param string
     */
    private void writeMsgToClient(OutputStream outputStream, String string) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        writer.append("I am server message!!!");
        writer.flush();
        writer.close();
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Boolean call() throws Exception {
        try {

            System.out.println("****received message from client******");

            //读取客户端传过来的数据
            readMessageFromClient(connection.getInputStream());

            System.out.println("****received message from client end******");
            System.out.println();

            //向客户端写入数据
            writeMsgToClient(connection.getOutputStream(), "I am server message!!!");

//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
