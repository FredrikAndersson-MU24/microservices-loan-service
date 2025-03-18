package com.example.LoanMicro;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long LoanId;

    private Long bookId;

    private boolean availability;

    private String borrowerName;

    private Date loanDate;

    private Date returnDate;

}
