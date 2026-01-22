package com.example.website.repository;

import com.example.website.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    public WalletTransaction findByRazorpayPaymentId(String razorpayPaymentId);
}

