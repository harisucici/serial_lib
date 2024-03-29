package com.harisucici.lib.serial.comn;

import android.os.SystemClock;

import com.licheedev.myutils.LogPlus;
import com.serial.comn.message.LogManager;
import com.serial.util.ByteUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读串口线程
 */
public class SerialReadThread extends Thread {

    private static final String TAG = "SerialReadThread";

    private BufferedInputStream mInputStream;
    private boolean mWorking=true;

    public SerialReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }

    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;

        LogPlus.e("开始读线程");

        while (mWorking) {

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                int available = mInputStream.available();
                if (available > 0) {
                    Thread.sleep(500);//拼包
                    size = mInputStream.read(received);
                    if (size > 0) {
                        onDataReceive(received, size);
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
                LogPlus.e("读取数据失败", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Thread.yield();
        }

        LogPlus.e("结束读进程");
    }

    /**
     * 处理获取到的数据
     *
     * @param received
     * @param size
     */
    private void onDataReceive(byte[] received, int size) {
        // TODO: 2018/3/22 解决粘包、分包等
        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);
//        LogManager.instance().post(new RecvMessage(hexStr));
        LogManager.instance().post(hexStr);
    }

    /**
     * 停止读线程
     */
//    public void close() {
//
//        try {
//            mInputStream.close();
//        } catch (IOException e) {
//            LogPlus.e("异常", e);
//        } finally {
//            super.interrupt();
//        }
//    }

    public void close() {
        if (mWorking) {
            mWorking = false;
        }
    }
}
