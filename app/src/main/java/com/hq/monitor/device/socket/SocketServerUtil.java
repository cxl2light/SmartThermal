package com.hq.monitor.device.socket;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hq.base.app.CommonExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 2020/6/7
 * author :
 * desc :
 */
public class SocketServerUtil {
    private static final String TAG = "SocketServerUtil";
    public static final int SERVER_SOCKET_PORT = 6666;
    private static volatile boolean serverReady = false;
    private static volatile ServerSocketChannel mSsc = null;
    private static final CopyOnWriteArrayList<SocketHandler> SOCKET_HANDLERS = new CopyOnWriteArrayList<>();

    public static void start() {
        CommonExecutor.getExecutorService().execute(() -> {
            try {
                final ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(SERVER_SOCKET_PORT));
                ssc.configureBlocking(false);
                final Selector selector = Selector.open();
                ssc.register(selector, SelectionKey.OP_ACCEPT);
                mSsc = ssc;
                serverReady = true;
                while (selector.select() > 0) {
                    final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        final SelectionKey key = iterator.next();
                        iterator.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            final ServerSocketChannel s = (ServerSocketChannel) key.channel();
                            if (s != null) {
                                Log.d(TAG, "收到客户端连接：" + s.toString());
                                final SocketHandler socketHandler = new SocketHandler(s.accept());
                                SOCKET_HANDLERS.add(socketHandler);
                                SocketHandlerExecutor.getExecutorService().execute(socketHandler);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "创建服务端异常：" + e.toString());
            }
        });
    }

    public static void destroy() {
        try {
            if (mSsc != null) {
                mSsc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (SocketHandler handler : SOCKET_HANDLERS) {
            handler.destroy();
        }
        SOCKET_HANDLERS.clear();
    }

    public static boolean isServerReady() {
        return serverReady;
    }

    private static class SocketHandler implements Runnable {
        private static final int READ_BUFFER_SIZE = 256;
        private static final int WRITE_BUFFER_SIZE = 512;

        private final SocketChannel mSocketChannel;
        private final ByteBuffer mReadBuffer;
        private final ByteBuffer mWriteBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
        private final byte[] mByteArr = new byte[READ_BUFFER_SIZE];

        private final LinkedList<String> mReadCommand = new LinkedList<>();
        private volatile boolean mDestroy = false;

        public SocketHandler(@NonNull SocketChannel socketChannel) {
            mSocketChannel = socketChannel;
            mReadBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        }

        public void destroy() {
            mDestroy = true;
            try {
                mSocketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            int bytesRead;
            final ByteBuffer readBuffer = mReadBuffer;
            try {
                mSocketChannel.configureBlocking(false);
                final Selector selector = Selector.open();
                mSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                while (selector.select() > 0) {
                    if (mDestroy) {
                        return;
                    }
                    final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        if (mDestroy) {
                            return;
                        }
                        final SelectionKey key = iterator.next();
                        iterator.remove();
                        final SocketChannel socketChannel = (SocketChannel) key.channel();
                        if (key.isReadable()) {
                            bytesRead = socketChannel.read(readBuffer);
                            readBuffer.flip();
                            readBuffer.get(mByteArr, 0, bytesRead);
                            final String result = new String(mByteArr, 0, bytesRead, StandardCharsets.UTF_8);
                            mReadCommand.offer(result);
                            readBuffer.clear();
                        }
                        if (key.isWritable()) {
                            final String command = mReadCommand.poll();
                            if (command == null) {
                                continue;
                            }
                            mWriteBuffer.clear();
                            mWriteBuffer.put(("设备：" + command).getBytes(StandardCharsets.UTF_8));
                            mWriteBuffer.flip();
                            while (mWriteBuffer.hasRemaining()) {
                                mSocketChannel.write(mWriteBuffer);
                            }
                            mWriteBuffer.clear();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
