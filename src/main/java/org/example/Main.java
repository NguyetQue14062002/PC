package org.example;
//package socket;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {
        // Nội dung string gửi từ Raspberry Pi --> PC
        String stringFromRaspberry;
        String check;

        // Tạo socket PC, chờ tại cổng '6543'
        ServerSocket welcomeSocket = new ServerSocket(6543);

        while (true){
            // Chờ yêu cầu từ Raspberry
            Socket connectionSocket = welcomeSocket.accept();

            // Tạo input stream, nối tới Socket Raspberry
            BufferedReader inFromRaspberry = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));

            // Tạo outputStream, nối với socket Raspberry
            DataOutputStream outToRaspberry = new DataOutputStream(connectionSocket.getOutputStream());

            // Đọc thông tin từ socket Raspberry
            stringFromRaspberry = inFromRaspberry.readLine();

            // In kết quả ra màn hình
            System.out.println("Data Raspberry Pi --> PC: " + stringFromRaspberry + " (Done)\n");

            // Ghi dữ liệu từ PC --> socket Raspberry Pi (để thông báo chuỗi đã truyền thành công)
            String stringToRaspberry = stringFromRaspberry + "\n";
            outToRaspberry.write((stringToRaspberry).getBytes("UTF-8"));

            BufferedReader CheckFromRaspberry = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));

            check = CheckFromRaspberry.readLine();
            System.out.println("Data Raspberry Pi --> PC: " + check  + " (Done)\n");
            //return;
        }
    }
}