package com.example.LoanMicro;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponseDTO {
    private Loan loan;
    
    private Book book;
    
    public LoanResponseDTO(Loan loan,Book book){
        this.loan= loan;
        this.book= book;
    }
    public LoanResponseDTO(){
        
    }
}
