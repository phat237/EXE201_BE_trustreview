//package com.trustreview.trustreview.API;
//
//import com.trustreview.trustreview.Service.PayOSService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/payos")
//@RequiredArgsConstructor
//
//public class PayOSPaymentController {
//
//    private final PayOSService payOSService;
//
//    @PostMapping("/create-link")
//    public ResponseEntity<?> createPaymentLink() throws Exception {
//        return ResponseEntity.ok(payOSService.createPayment());
//    }
//}