package org.example.Controller;

import org.example.DTO.dataReadDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.Controller.SecondAPIHandler.callSecondAPI;

@RestController
@RequestMapping("/api")
public class SocketController {
    private ExecutorService executorService;
    private Socket latestConnectionSocket;

    private ServerSocket welcomeSocket;

    public SocketController() throws IOException {
        welcomeSocket = new ServerSocket(6543);
        executorService = Executors.newFixedThreadPool(10);
        listenForConnections();
    }

    private void listenForConnections() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Socket connectionSocket = welcomeSocket.accept();
                    latestConnectionSocket = connectionSocket;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private String secondAPICallResult;
    @PostMapping("/verify")
    public ResponseEntity<String> handleSocketRequest(@RequestBody Map<String, String> requestBody) {
        try {
            String barGuid = requestBody.get("barGuid");
            Socket connectionSocket = latestConnectionSocket;
            latestConnectionSocket = null;

            if (connectionSocket != null && barGuid !=null) {
                dataReadDTO result = handleConnection(connectionSocket);
                if(barGuid.equals(result.getUid())) {
                    secondAPICallResult = "{ Chuỗi ghi lên tem = "+ barGuid+  "\nChuỗi đọc từ tem = " +result.getUid() + "\nHai chuỗi giống nhau}";
                } else {
                    secondAPICallResult = "{ Chuỗi ghi lên tem = "+ barGuid+  "\nChuỗi đọc từ tem = " +result.getUid() + "\nHai chuỗi khác nhau}";

                }

                   return ResponseEntity.ok(secondAPICallResult );
            } else {
                System.out.println("No connection available");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private dataReadDTO handleConnection(Socket connectionSocket) throws IOException {

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
        dataReadDTO data = new dataReadDTO();
        data.setUid(stringFromRaspberry);
        data.setStatus(check);
        return data;
    }

    @GetMapping("/get/secondapiresponse")
    public ResponseEntity<String> getSecondAPIResponse() {
        if (secondAPICallResult != null) {
            return new ResponseEntity<>(secondAPICallResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}