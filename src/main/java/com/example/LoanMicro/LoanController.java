package com.example.LoanMicro;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .uri("/book/" + loan.getBookId() + "/false").retrieve().toBodilessEntity().block();
        System.out.println("/book/" + loan.getBookId() + "/false");
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

}
