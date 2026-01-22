package com.example.website.service;

import com.example.website.entity.Registration;
import com.example.website.entity.Wallet;
import com.example.website.entity.WalletTransaction;
import com.example.website.repository.RegistrationRepository;
import com.example.website.repository.WalletRepository;
import com.example.website.repository.WalletTransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class WalletService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    private final RegistrationRepository registrationRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository transactionRepository,
                         RegistrationRepository registrationRepository
    ) {
        this.registrationRepository=registrationRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }


    @PostConstruct
    public void init() throws Exception {
        this.razorpayClient = new RazorpayClient(
                razorpayKeyId,
                razorpayKeySecret
        );
    }


    public Wallet createWallet(Registration registration) {
        Wallet wallet = new Wallet();
        wallet.setRegistration(registration);
        wallet.setBalance(0.0);
        wallet.setLockedBalance(0.0);
        return walletRepository.save(wallet);
    }


    public Order createAddMoneyOrder(Long registrationId, Double amount) {

        try {
            Optional<Registration> optionalRegistration=registrationRepository.findById(registrationId);
            if (optionalRegistration.isEmpty()){
                throw new RuntimeException("user ID not found");
            }
            Registration registration=optionalRegistration.get();
            Wallet wallet;
            Optional<Wallet> optionalWallet=walletRepository.findByRegistrationId(registrationId);
            wallet = optionalWallet.orElseGet(() -> createWallet(registration));

            WalletTransaction newtx=new WalletTransaction();
            newtx.setWallet(wallet);
            newtx.setType("ADD");
            newtx.setStatus("PENDING");
            newtx.setAmount(amount);
            WalletTransaction savedTransaction = transactionRepository.save(newtx);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "wallet_" + savedTransaction.getId());
            orderRequest.put("payment_capture", 1);

            Order order=razorpayClient.orders.create(orderRequest);

            newtx.setReceiptID("wallet_" + savedTransaction.getId());
            newtx.setRazorpayPaymentId(order.get("id"));
            transactionRepository.save(newtx);

            return order;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }


    public void verifyPaymentAndAddMoney(
            Long registrationId,
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature,
            Double amount
    ) {

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(
                    options,
                    razorpayKeySecret
            );

            if (!isValid) {
                throw new RuntimeException("Invalid Razorpay signature");
            }

            WalletTransaction transaction=transactionRepository.findByRazorpayPaymentId(razorpayOrderId);
            transaction.setStatus("SUCCESS");
            transactionRepository.save(transaction);
            Wallet wallet = transaction.getWallet();
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed", e);
        }
    }

    public void withdrawMoneyRequest(Wallet wallet, Double amount){
        wallet.setPendingWithdrawn(wallet.getPendingWithdrawn()+amount);

        WalletTransaction transaction=new WalletTransaction();
        transaction.setStatus("PENDING");
        transaction.setType("WITHDRAW");
        transaction.setAmount(amount);
        transactionRepository.save(transaction);
    }

    public void withdrawStatusUpdate(Long transactionId,String status){
        Optional<WalletTransaction> opTransaction = transactionRepository.findById(transactionId);
        if (opTransaction.isEmpty()){
            return;
        }
        WalletTransaction transaction=opTransaction.get();
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }


    public Wallet getWallet(Long registrationId) {
        return walletRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}
