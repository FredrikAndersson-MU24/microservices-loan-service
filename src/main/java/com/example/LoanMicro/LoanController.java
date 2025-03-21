package com.example.LoanMicro;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
                .uri("/book/loan/" + loan.getBookId())
                .retrieve()
                .onStatus(HttpStatusCode.valueOf(404)::equals, _ -> Mono.error(new EntityNotFoundException("Book ID not found (response from book) (from loan)")))
                .onStatus(HttpStatus.BAD_REQUEST::equals, _ -> Mono.error(new BadRequestException("Book is not available (response from book)(from loan) ")))
                .toBodilessEntity()
                .block();
        return loanRepository.save(loan);
    }

    @GetMapping("/{id}")
    public Mono<LoanResponseDTO> getLoanById(@PathVariable Long id) {
        boolean exists = loanRepository.existsById(id);
        if (exists) {
            return loanRepository.findById(id)
                    .map(loan ->
                            bookClient.get()
                                    .uri("/book/" + loan.getBookId())
                                    .retrieve()
                                    .bodyToMono(Book.class)
                                    .map(book ->
                                            new LoanResponseDTO(loan, book))
                    ).orElse(Mono.empty());
        } else throw new EntityNotFoundException("Loan ID not found");
    }

    @PutMapping("/return/{loanId}")
    public Loan returnLoan(@PathVariable Long loanId) throws BadRequestException {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null) {
            if (loan.isReturned()) {
                throw new BadRequestException("Loan ID " + loan.getLoanId() + " is already returned!");
            }
            loan.setReturned(true);
            bookClient.put()
                    .uri("/book/return/" + loan.getBookId())
                    .retrieve()
                    .onStatus(HttpStatusCode.valueOf(404)::equals, _ -> Mono.error(new EntityNotFoundException("Book ID not found (response from book) (from loan)")))
                    .onStatus(HttpStatus.BAD_REQUEST::equals, _ -> Mono.error(new BadRequestException("Book is not available (response from book)(from loan) ")))
                    .toBodilessEntity()
                    .block();
            return loanRepository.save(loan);
        } else throw new EntityNotFoundException("Loan ID not found");
    }

}
