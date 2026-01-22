package com.example.website.controller;

import com.example.website.dto.NewOrder;
import com.example.website.dto.VerifyPayment;
import com.example.website.entity.Wallet;
import com.example.website.entity.WalletTransaction;
import com.example.website.repository.WalletTransactionRepository;
import com.example.website.service.WalletService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/wallet")
public class WalletController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletController(WalletService walletService,WalletTransactionRepository transactionRepository){
        this.walletService=walletService;
        this.transactionRepository=transactionRepository;
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

    @PostMapping("/withdraw")
    public ResponseEntity<?> withDrawMoney(
            Authentication authentication,
            @RequestBody Map<?,?> body
    ){
        Long userId = (Long) authentication.getCredentials();
        Wallet wallet =walletService.getWallet(userId);
        if (wallet == null){
            return ResponseEntity.notFound().build();
        }
        Double amount= (Double) body.get("amount");

        if(!body.containsKey("amount")){
            return ResponseEntity.badRequest().body(Map.of("message","invalid amount"));
        }

        if(amount > wallet.getBalance()){
            return ResponseEntity.badRequest().body(Map.of("message","insufficient balance"));
        }

        walletService.withdrawMoneyRequest(wallet, amount);
        return ResponseEntity.ok(body);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllTransactions(
            Authentication authentication
    ){
        Long userId = (Long) authentication.getCredentials();
        Wallet wallet =walletService.getWallet(userId);
        List<WalletTransaction> transactionList=transactionRepository.findByWallet(wallet);
        return ResponseEntity.ok(
                Map.of("wallet",wallet,"transactions",transactionList)
        );
    }

}
