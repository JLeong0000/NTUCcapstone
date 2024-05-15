package com.bank.uob.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "uob_transactions")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trans_id;
    private String trans_type;
    private double trans_amt;
    private String teller_name;

    @ManyToOne
    @JoinColumn(name = "acc_id")
    private Accounts accs;

    public Transactions(String trans_type, double trans_amt, String teller_name, Accounts accs) {
        this.trans_type = trans_type;
        this.trans_amt = trans_amt;
        this.teller_name = teller_name;
        this.accs = accs;
    }

}
