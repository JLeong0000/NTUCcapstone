package com.bank.uob.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "uob_accs")
public class Accounts {
    @Id
    private int acc_id;
    private String acc_name;
    private double acc_bal;
    private String teller_name;
}
