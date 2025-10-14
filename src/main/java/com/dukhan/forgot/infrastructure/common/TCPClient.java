package com.dukhan.forgot.infrastructure.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

@Component
public class TCPClient {

    @Value("${hsm.mock:true}") // true = mock mode by default
    private boolean mockMode;

    private String ipAddress;
    private int port;
    private int socketReadTimeout;

    public String executeCommand(String command) {
        if (mockMode) {
            String refNum = command.substring(0, 4); // reuse request refNum
            String status = "00"; // success
            String encrypted = "PIN" + System.currentTimeMillis(); // dynamic fake PIN
            return refNum + "00" + status + encrypted;
        }

        try (Socket socket = new Socket(ipAddress, port)) {
            socket.setSoTimeout(socketReadTimeout);
            OutputStream out = socket.getOutputStream();
            out.write(command.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with HSM: " + e.getMessage(), e);
        }
    }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setPort(int port) { this.port = port; }
    public void setSocketReadTimeout(int socketReadTimeout) { this.socketReadTimeout = socketReadTimeout; }
}
