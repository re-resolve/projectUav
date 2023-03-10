package com.example.projectUav.common.Transmit;

import com.example.projectUav.common.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Handler implements Runnable {
    private Socket socket = null;        //初始化Socket
    
    public Handler(Socket socket) {
        this.socket = socket;            //传入参数
    }
    
    /*
     *	输出流
     */
    public PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }
    
    /*
     *输入流
     */
    public BufferedReader getReader(Socket socket) throws IOException {
        InputStreamReader socketIn = new InputStreamReader(socket.getInputStream());
        return new BufferedReader(socketIn);
    }
    
    public void run() {
        try {
            //打印与前端新连接的信息
            log.info("New Connection " + socket.getInetAddress() + "：" + socket.getPort());
    
            BufferedReader br = getReader(socket);    //输入流
            PrintWriter pw1 = getWriter(Client.socket);        //输出流
            
            String msg = null;        //初始化msg
            while ((msg = br.readLine()) != null) {    //循环读取一行信息
                log.info(msg);            //打印信息
                //将前端发送的转发给安卓端
                pw1.println(msg);                //将信息加工发送回客户端
                //Client.socket.getOutputStream().write((msg+"\n").getBytes(StandardCharsets.UTF_8));
                //Client.socket.getOutputStream().flush();
                if (msg.equals("exit")) {            //结束条件
                    break;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {            //如有连接，关闭
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
