package com.example.website.controller;

import com.example.website.dto.NewOrder;
import com.example.website.dto.VerifyPayment;
import com.example.website.service.WalletService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/wallet")
public class WalletController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService=walletService;
    }


    @PostMapping("/order-add-money")
    public ResponseEntity<?> orderRequest(
            Authentication authentication,
            @RequestBody NewOrder order
    ) {
        Long userId = (Long) authentication.getCredentials();

        Order rpOrder = walletService.createAddMoneyOrder(userId, order.getAmount());

        return ResponseEntity.ok(
                Map.of("orderId",rpOrder.get("id"),
                        "amount",order.getAmount(),
                        "currency",rpOrder.get("currency"),
                        "key",razorpayKeyId
                )
        );
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyOrder(
            Authentication authentication,
            @RequestBody VerifyPayment payment
    ){
        Long userId = (Long) authentication.getCredentials();
        walletService.verifyPaymentAndAddMoney(userId,payment.razorpayOrderId,payment.razorpayPaymentId,payment.razorpaySignature,payment.amount);
        return ResponseEntity.ok(Map.of("message","ok"));
    }



}
