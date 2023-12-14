package org.example.Controller;
import java.util.UUID;

import org.example.DTO.BarcodeGuid;
import org.example.DTO.RequestBodyModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateBarGuid{
    private String generateGUID() {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/api/barguid")
    public ResponseEntity<BarcodeGuid> handlePostRequest(@RequestBody RequestBodyModel requestBody) {
        try {
            String guid = generateGUID();
            String barcode = requestBody.getBarcode();
            String barGuid = barcode + " " + guid;
            BarcodeGuid res = new BarcodeGuid();
            res.setBarguid(barGuid);
            res.setStatus(0);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            BarcodeGuid errorRes = new BarcodeGuid();
            errorRes.setStatus(-1);
            return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}