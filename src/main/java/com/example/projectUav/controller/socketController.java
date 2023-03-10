package com.example.projectUav.controller;

import com.example.projectUav.common.Result;
import com.example.projectUav.common.Transmit.Client;
import com.example.projectUav.common.Transmit.transmitServer;
import com.example.projectUav.common.TransmitNetty.ClientNetty;
import com.example.projectUav.common.TransmitNetty.transmitServerNetty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
@RestController
@RequestMapping("socket")
public class socketController {
    
    private Integer portForShutdown = 8082;
    
    private String host = "localhost";
    
    //安卓端ip
    private static String AndroidHost = "localhost";
    
    //安卓端port
    private static Integer portForAndroid = 8000;
    
    private static transmitServer server = null;
    
    private static transmitServerNetty serverNetty = null;
    
    /**
     * 开启转发消息功能，实时控制无人机
     */
    @GetMapping("startConnection")
    public Result<String> startConnection() throws IOException {
        
        if(server!=null){
            return Result.error("服务已开启，不可重复开启");
        }
        //后端与安卓端的socket
        try {
            Client.socket = new Socket(AndroidHost, portForAndroid);
            Client.socket.setSoTimeout(900000);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("无法连接到安卓端");
        }
    
        server = new transmitServer();
        
        server.service();
        
        return Result.success("转发消息功能开启成功");
    }
    
    /**
     * 关闭开启转发消息功能（先关闭所有服务）
     *
     * @return
     */
    @GetMapping("stopConnection")
    public Result<String> stopConnection() {
        if (!server.getExecutorService().isTerminated()) {
            return Result.error("当前仍有服务未关闭，请先关闭所有服务");
        } else {
            Socket socket = null;
            try {
                socket = new Socket(host, portForShutdown);
                OutputStream socketOut = socket.getOutputStream();
                socketOut.write("shutdown\r\n".getBytes());
    
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = null;
                while ((msg = br.readLine()) != null) {
                    log.info(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    //关闭后端与安卓端之间的socket
                    if (Client.socket != null) {
                        Client.socket.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                    server = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Result.success("成功停止与无人机的实时连接");
    
        }
    }
    
    /**
     * 测试 Netty版
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // 创建一个服务端
        serverNetty = new transmitServerNetty(8083);
        // 创建客户端（连接安卓端）
        if (ClientNetty.channel==null) {
            System.out.println("channel==null");
            ClientNetty clientNetty = new ClientNetty();
            clientNetty.setClientNetty(AndroidHost,portForAndroid);
        }
    }
}
