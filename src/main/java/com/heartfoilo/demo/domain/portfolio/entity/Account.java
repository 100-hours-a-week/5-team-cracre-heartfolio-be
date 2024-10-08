package com.heartfoilo.demo.domain.portfolio.entity;


import com.heartfoilo.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cash", nullable = false)
    private Long cash;

    @Column(name = "total_purchase", nullable = false)
    private Long totalPurchase;

    @Column(name = "donation_payment", nullable = false)
    private long donationPayment;
    public Account(User newUser, long cash, long totalPurchase,long donationPayment) {
        this.user = newUser;
        this.cash = cash;
        this.totalPurchase = totalPurchase;
        this.donationPayment = donationPayment;
    }

    public Account(User newUser, long cash, long totalPurchase) {
        this.user = newUser;
        this.cash = cash;
        this.totalPurchase = totalPurchase;
    }


    public void ChangeCash(Long additionalCash) {
        if (this.cash == null) {
            this.cash = 0L;  // null일 경우 초기값을 설정
        }
        this.cash += additionalCash;

    }
    public void ChangeTotalPurchase(Long Value){
        this.totalPurchase -= Value;
        if (this.totalPurchase < 0){
            this.totalPurchase = 0L;
        }
    }
    public void BuyStock(Long totalAmount){
        this.cash -= totalAmount;
        this.totalPurchase += totalAmount;
    }
    public void ResetTotalPurchase(){
        this.totalPurchase = 0L;
    }


    public void ChangeDonationPayment(long donationPayment){
        this.donationPayment += donationPayment;
    }
}