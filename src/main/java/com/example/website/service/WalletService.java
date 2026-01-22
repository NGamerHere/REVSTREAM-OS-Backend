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


    public void lockMoney(Long registrationId, Double amount) {

        Wallet wallet = getWallet(registrationId);

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setLockedBalance(wallet.getLockedBalance() + amount);
        walletRepository.save(wallet);

        saveTransaction(wallet, "LOCK", amount, null, "SUCCESS");
    }


    public void releaseMoney(
            Long payerRegistrationId,
            Long receiverRegistrationId,
            Double amount
    ) {

        Wallet payerWallet = getWallet(payerRegistrationId);
        Wallet receiverWallet = getWallet(receiverRegistrationId);

        if (payerWallet.getLockedBalance() < amount) {
            throw new RuntimeException("Insufficient locked balance");
        }

        payerWallet.setLockedBalance(
                payerWallet.getLockedBalance() - amount
        );
        receiverWallet.setBalance(
                receiverWallet.getBalance() + amount
        );

        walletRepository.save(payerWallet);
        walletRepository.save(receiverWallet);

        saveTransaction(payerWallet, "RELEASE", amount, null, "SUCCESS");
    }


    public void refundLockedMoney(Long registrationId, Double amount) {

        Wallet wallet = getWallet(registrationId);

        if (wallet.getLockedBalance() < amount) {
            throw new RuntimeException("No locked money to refund");
        }

        wallet.setLockedBalance(wallet.getLockedBalance() - amount);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        saveTransaction(wallet, "REFUND", amount, null, "SUCCESS");
    }


    private Wallet getWallet(Long registrationId) {
        return walletRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    private void saveTransaction(
            Wallet wallet,
            String type,
            Double amount,
            String razorpayPaymentId,
            String status
    ) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setRazorpayPaymentId(razorpayPaymentId);
        tx.setStatus(status);

        transactionRepository.save(tx);
    }
}
