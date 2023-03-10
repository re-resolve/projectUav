package com.example.projectUav.common.Transmit;

import com.example.projectUav.common.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 接收前台数据的服务端（基于Scoket）
 */
@Slf4j
public class transmitServer {
    private int port = 8084;        //服务端口
    
    private ServerSocket serverSocket = null;    //服务Socket
    
    
    private final int POOL_SIZE = 4;            //单个CPU的线程数
    
    //守护线程端口
    private int portForShutdown = 8082;            //供管理程序连接，当发送特定字符时，服务器停止向线程池添加任务并等待任务执行完毕或超时，关闭服务程序。
    
    private ServerSocket serverSocketForShutdown = null;    //守护Socket
    
    private boolean isShutdown = false;            //服务器是否关闭
    
    private ExecutorService executorService = null;    //线程池(异步)
    
    public ExecutorService getExecutorService() {
        return executorService;
    }
    /**
     *
     * @throws IOException
     */
    public transmitServer() throws IOException {
        //创建Socket
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);    //设置超时时间
        //创建守护Socket
        serverSocketForShutdown = new ServerSocket(portForShutdown);
        //创建线程池
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        //运行守护线程
        shutDownThread.start();
        log.info("transmitServer Up");
        
    }
    
    public void service() {
        while (!isShutdown) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                socket.setSoTimeout(60000);
                executorService.execute(new Handler(socket));
            } catch (SocketTimeoutException e) {
                log.error("Client Timeout");
            } catch (RejectedExecutionException e) {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException x) {
                    return;
                }
            } catch (SocketException e) {
                if (e.getMessage().contains("socket closed")) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 负责关闭服务器的线程
     */
    private Thread shutDownThread = new Thread() {
        public void start() {
            this.setDaemon(true);    //设置为守护线程（后台线程）
            super.start();
        }
        
        public void run() {
            while (!isShutdown) {
                Socket socketForShutdown = null;
                try {
                    socketForShutdown = serverSocketForShutdown.accept();    //开启监听
                    //获取输入流
                    BufferedReader br = new BufferedReader(new InputStreamReader(socketForShutdown.getInputStream()));
                    
                    String command = br.readLine();    //读取一行字符
                    
                    //判断是否符合指定字符
                    if (command.equals("shutdown")) {
                        
                        long beginTime = System.currentTimeMillis();    //开始计数
                        
                        //输出流输出字符
                        socketForShutdown.getOutputStream().write("transmitServer Shutdowning\r\n".getBytes());
                        
                        //服务器关闭
                        isShutdown = true;
                        
                        //不再向线程池添加新线程
                        executorService.shutdown();
                        
                        //所有任务是否已完成(超时的话，可以强行关闭)
                        while (!executorService.isTerminated()) {
                            //一个executorService中有多个线程及每个线程的任务
                            log.info(executorService.toString());
                            //设置线程池中任务的完成超时时间
                            executorService.awaitTermination(60, TimeUnit.SECONDS);
                        }
                        //关闭服务器
                        serverSocket.close();
                        
                        long endTime = System.currentTimeMillis();    //结束计数
                        
                        //输出流输出字符
                        socketForShutdown.getOutputStream().write(("transmitServer Shutdown\r\nTime：" + (endTime - beginTime) + "ms\r\n").getBytes());
                        
                        //关闭守护线程
                        socketForShutdown.close();
                        serverSocketForShutdown.close();
                    } else {
                        //不符合特定字符
                        socketForShutdown.getOutputStream().write("Error".getBytes());
                        socketForShutdown.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CustomException("无法正常停止与无人机的连接");
                }
            }
        }
    };
    

}
