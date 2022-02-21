package com.hq.monitor.device.socket;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.hq.base.app.CommonExecutor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.hq.monitor.device.socket.SocketServerUtil.SERVER_SOCKET_PORT;

/**
 * Created on 2020/6/7
 * author :
 * desc :
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    private static final int BUFFER_SIZE = 512;

    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final ByteBuffer mWriteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final byte[] mByteArr = new byte[BUFFER_SIZE];
    private final WeakReference<Handler> mHandlerReference;
    private final SocketClientCallback mSocketClientCallback;
    private volatile SocketChannel mSocketChannel;
    private volatile boolean mDestroyed = false;

    public SocketClient(@NonNull Handler handler, @NonNull SocketClientCallback socketClientCallback) {
        mHandlerReference = new WeakReference<>(handler);
        mSocketClientCallback = socketClientCallback;
    }

    public synchronized void connect() {
        CommonExecutor.getExecutorService().execute(() -> {
            try {
                realConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @WorkerThread
    private void realConnect() throws IOException {
        if (mSocketChannel != null) {
            if (mSocketChannel.isConnected()) {
                return;
            }
            mSocketChannel.close();
        }
        while (!SocketServerUtil.isServerReady()) {
            if (mDestroyed) {
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mDestroyed) {
            return;
        }
        mSocketChannel = SocketChannel.open();
        final SocketChannel sc = mSocketChannel;
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(SERVER_SOCKET_PORT));
        while (!sc.finishConnect()) {
            if (mDestroyed) {
                return;
            }
            Log.d(TAG, "等待非阻塞连接建立....");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Handler handler;
        synchronized (mHandlerReference) {
            handler = mHandlerReference.get();
            if (handler != null) {
                handler.post(mSocketClientCallback::onConnectSuccess);
            }
        }
        int bytesRead;
        final Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_READ);
        while (selector.select() > 0) {
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                final SocketChannel socketChannel = (SocketChannel) key.channel();
                if (key.isReadable()) {
                    final ByteBuffer readBuffer = mReadBuffer;
                    readBuffer.clear();
                    bytesRead = socketChannel.read(readBuffer);
                    readBuffer.flip();
                    readBuffer.get(mByteArr, 0, bytesRead);
                    final String result = new String(mByteArr, 0, bytesRead, StandardCharsets.UTF_8);
                    readBuffer.clear();
                    Log.d(TAG, result);
                    synchronized (mHandlerReference) {
                        handler = mHandlerReference.get();
                        if (handler != null) {
                            handler.post(() -> mSocketClientCallback.onRead(result));
                        }
                    }
                }
            }
        }
    }

    public synchronized void sentCommand(String command) {
        SocketHandlerExecutor.getExecutorService().execute(() -> {
            if (mSocketChannel == null || !mSocketChannel.isConnected()) {
                return;
            }
            mWriteBuffer.clear();
            mWriteBuffer.put(command.getBytes(StandardCharsets.UTF_8));
            try {
                while (mWriteBuffer.hasRemaining()) {
                    mSocketChannel.write(mWriteBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void destroy() {
        synchronized (mHandlerReference) {
            mHandlerReference.clear();
        }
        mDestroyed = true;
        if (mSocketChannel != null && mSocketChannel.isConnected()) {
            try {
                mSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSocketChannel = null;
    }

    public interface SocketClientCallback {

        @MainThread
        void onConnectSuccess();

        @MainThread
        void onRead(String data);

    }

}
