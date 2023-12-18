package org.example.Controller;

import org.example.DTO.RequestBodyModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.Controller.SecondAPIHandler.callSecondAPI;

@RestController
@RequestMapping("/api")
public class SocketController {
    private ExecutorService executorService;
    private Socket latestConnectionSocket;
    private final Object lock = new Object(); // Đối tượng lock

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
                    synchronized (lock) { // Sử dụng synchronized để đồng bộ hóa
                        latestConnectionSocket = connectionSocket;
                    }
                    //  handleConnection(connectionSocket);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private String secondAPICallResult;

    private String generateGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @PostMapping("/create/barguid")
    public ResponseEntity<String> handlePostRequest(@RequestBody RequestBodyModel requestBody) {
        try {
            String guid = generateGUID();
            String barcode = requestBody.getBarcode();
            Socket connectionSocket;
            synchronized (lock) {
                connectionSocket = latestConnectionSocket;
            }
            if (connectionSocket != null) {
                String barGuid = guid + " " + barcode;

                Socket tempSocket = connectionSocket; // Tạo một biến tạm để sử dụng trong callSecondAPI

                 sendToRaspberry(tempSocket, barGuid);
              //  connectionSocket.close();
                callSecondAPI(barGuid);
                return ResponseEntity.ok(barGuid);
            } else {
                System.out.println("No connection available");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return new ResponseEntity<>("errorRes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> receiveFromRaspberryAPI(@RequestBody Map<String, String> requestBody) {
        try {
            Socket connectionSocket =  latestConnectionSocket; // Tạo kết nối đến Raspberry Pi
            String receivedData = receiveFromRaspberry(connectionSocket); // Nhận dữ liệu từ Raspberry Pi
            String barGuid = requestBody.get("barGuid");
            System.out.println("Du lieu len tem:" + barGuid +"\n Du lieu doc tu tem :" +receivedData);
            if(barGuid.equals(receivedData))
            {
                secondAPICallResult = "Hai chuỗi giống nhau";
                connectionSocket.close(); // Đóng kết nối socket
            }
            else {
                secondAPICallResult = "Hai chuỗi khác nhau";
                connectionSocket.close(); // Đóng kết nối socket
            }
            return ResponseEntity.ok(secondAPICallResult );
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/secondapiresponse")
    public ResponseEntity<String> getSecondAPIResponse() {
        if (secondAPICallResult != null) {
            return new ResponseEntity<>(secondAPICallResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    private void sendToRaspberry(Socket connectionSocket, String barGuid) throws IOException {
        DataOutputStream outToRaspberry = new DataOutputStream(connectionSocket.getOutputStream());
        String stringToRaspberry = barGuid + "\n";
        outToRaspberry.write((stringToRaspberry).getBytes("UTF-8"));
        outToRaspberry.flush();
        System.out.println("Đã gửi dữ liệu từ PC đến Raspberry Pi");
    }
        private String receiveFromRaspberry(Socket connectionSocket) throws IOException {
            BufferedReader checkFromRaspberry = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
            String check = checkFromRaspberry.readLine();
            System.out.println("Đã nhận dữ liệu từ Raspberry Pi");
            System.out.println("Data Raspberry Pi --> PC: " + check + " (Done)\n");
            return check;
        }

}