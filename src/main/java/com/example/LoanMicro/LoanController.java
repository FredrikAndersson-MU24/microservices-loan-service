package com.example.LoanMicro;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.rmi.ServerException;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final WebClient bookClient;
    private final LoanRepository loanRepository;

    public LoanController(WebClient.Builder bookClientBuilder, LoanRepository loanRepository) {
        this.bookClient = bookClientBuilder.baseUrl("http://localhost:8081").build();
        this.loanRepository = loanRepository;
    }

    @PostMapping
    public Loan createLoan(@RequestBody Loan loan) {
        bookClient.put()
                .uri("/book/" + loan.getBookId() + "/false")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class).map(EntityNotFoundException::new))
                .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class).map(ServerException::new))
                .toBodilessEntity().block();
        return loanRepository.save(loan);
    }

    @GetMapping("/{id}")
    public Mono<LoanResponseDTO> getLoanById(@PathVariable Long id) {
        return loanRepository.findById(id)
                .map(loan ->
                        bookClient.get()
                                .uri("/book/" + loan.getBookId())
                                .retrieve()
                                .bodyToMono(Book.class)
                                .map(book ->
                                        new LoanResponseDTO(loan, book))
                ).orElse(Mono.empty());
    }

    @PutMapping("/return/{loanId}")
    public Loan returnLoan(@PathVariable Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null) {
            loan.setReturned(true);
            bookClient.put()
                    .uri("/book/" + loan.getBookId() + "/true").retrieve().toBodilessEntity().block();
            return loanRepository.save(loan);
        } else throw new EntityNotFoundException("Loan ID not found");

    }
}
