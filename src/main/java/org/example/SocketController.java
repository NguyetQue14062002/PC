package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class SocketController {
    private ExecutorService executorService;
    private Socket latestConnectionSocket;

    private ServerSocket welcomeSocket;

    public SocketController() throws IOException {
        welcomeSocket = new ServerSocket(6543);
        executorService = Executors.newFixedThreadPool(10); // Số lượng luồng tối đa trong pool
        listenForConnections();
    }

    private void listenForConnections() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Socket connectionSocket = welcomeSocket.accept();
                    latestConnectionSocket = connectionSocket; // Lưu trữ kết nối mới nhất
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @GetMapping("/socket")
    public String handleSocketRequest() {
        try {
            Socket connectionSocket = latestConnectionSocket; // Lấy kết nối mới nhất
            latestConnectionSocket = null; // Đặt lại kết nối mới nhất thành null

            if (connectionSocket != null) {
                String checkValue = handleConnection(connectionSocket);
                return checkValue;
            } else {
                return "No connection available";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
    private String handleConnection(Socket connectionSocket) throws IOException {

        BufferedReader inFromRaspberry = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
        DataOutputStream outToRaspberry = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader CheckFromRaspberry = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));

        String stringFromRaspberry = inFromRaspberry.readLine();
        System.out.println("Data Raspberry Pi --> PC: " + stringFromRaspberry + " (Done)\n");

        String stringToRaspberry = stringFromRaspberry + "\n";
        outToRaspberry.write((stringToRaspberry).getBytes("UTF-8"));

        String check = CheckFromRaspberry.readLine();
        System.out.println("Data Raspberry Pi --> PC: " + check + " (Done)\n");

        connectionSocket.close();
        inFromRaspberry.close();
        outToRaspberry.close();
        CheckFromRaspberry.close();
        return check;
    }
}