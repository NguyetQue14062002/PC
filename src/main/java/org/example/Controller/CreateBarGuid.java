package org.example.Controller;
import java.io.Console;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.example.DTO.*;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.example.Controller.SecondAPIHandler.callSecondAPI;

@RestController
public class CreateBarGuid {
    private String secondAPICallResult;
    private String generateGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String cutBlock(String barGuid, int startIndex) {
        if (barGuid.length() >= startIndex + 16) {
            return barGuid.substring(startIndex, startIndex + 16);
        } else {
            return "";
        }
    }

    @PostMapping("/api/create/barguid")
    public ResponseEntity<BarcodeGuid> handlePostRequest(@RequestBody RequestBodyModel requestBody) {
        try {
            String guid = generateGUID();
            String barcode = requestBody.getBarcode();
            String barGuid = guid + " " + barcode;
//
//            // Kiểm tra nếu barcode lớn hơn 16 kí tự
//            if (barcode.length() != 16) {
//                BarcodeGuid errorRes = new BarcodeGuid();
//                errorRes.setBarguid("Barcode Error");
//                return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
//            }
//
//            String barGuid = guid + " " + barcode;
//
//            // Cắt chuỗi barGuid thành 3 chuỗi mỗi buổi gồm 16 ký tự
//            String block1 = cutBlock(barGuid, 0);
//            String block2 = cutBlock(barGuid, 16);
//            String block4 = cutBlock(barGuid, 33);
//
//            BarcodeGuid res = new BarcodeGuid();
//            res.setBarguid(barGuid);
//
//            // Tạo và gán giá trị cho các đối tượng BlockG1, BlockG2, BlockG3
//            BlockG1 blockG1 = new BlockG1();
//            blockG1.setValue(block1);
//            res.setBlockG1(blockG1);
//
//            BlockG2 blockG2 = new BlockG2();
//            blockG2.setValue(block2);
//            res.setBlockG2(blockG2);
//
//            BlockG4 blockG4 = new BlockG4();
//            blockG4.setValue(block4);
//            res.setBlockG4(blockG4);
            BarcodeGuid res = new BarcodeGuid();
            res.setBarguid(barGuid);
            callSecondAPI(barGuid);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            BarcodeGuid errorRes = new BarcodeGuid();
            return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

