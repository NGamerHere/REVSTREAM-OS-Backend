package com.example.website.repository;

import com.example.website.entity.Wallet;
import com.example.website.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    WalletTransaction findByRazorpayPaymentId(String razorpayPaymentId);

    List<WalletTransaction> findByWallet(Wallet wallet);
}

