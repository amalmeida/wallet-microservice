//package com.walletapp.integration;
//
//import com.walletapp.application.service.WalletService;
//import com.walletapp.domain.model.Wallet;
//import com.walletapp.infrastructure.persistence.JpaWalletRepository;
//import com.walletapp.infrastructure.persistence.WalletEntity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class WalletIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private WalletService walletService;
//
//    @Autowired
//    private JpaWalletRepository jpaWalletRepository;
//
//    private String baseUrl;
//    private UUID userId;
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/wallets";
//        userId = UUID.randomUUID();
//        jpaWalletRepository.deleteAll(); // Limpa o banco antes de cada teste
//    }
//
//    @Test
//    void shouldCreateWalletDepositFundsAndRetrieveBalance() {
//        // Passo 1: Criar uma carteira via endpoint
//        ResponseEntity<String> createResponse = restTemplate.postForEntity(
//                baseUrl + "?userId=" + userId,
//                null,
//                String.class
//        );
//        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        System.out.println("Resposta da criação da carteira: " + createResponse.getBody());
//        UUID walletId = UUID.fromString(createResponse.getBody());
//
//        // Passo 2: Depositar fundos com idempotencyKey
//        HttpHeaders headers = new HttpHeaders();
//        UUID idempotencyKey = UUID.randomUUID();
//        headers.set("Idempotency-Key", idempotencyKey.toString());
//        HttpEntity<String> depositRequest = new HttpEntity<>(null, headers);
//
//        ResponseEntity<Void> depositResponse = restTemplate.exchange(
//                baseUrl + "/" + walletId + "/deposit?amount=50.00",
//                HttpMethod.POST,
//                depositRequest,
//                Void.class
//        );
//        assertThat(depositResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Passo 3: Consultar saldo
//        ResponseEntity<BigDecimal> balanceResponse = restTemplate.getForEntity(
//                baseUrl + "/" + walletId + "/balance",
//                BigDecimal.class
//        );
//        assertThat(balanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(balanceResponse.getBody()).isEqualTo(BigDecimal.valueOf(50.00));
//
//        // Passo 4: Verificar persistência no banco
//        WalletEntity entity = jpaWalletRepository.findById(walletId).orElseThrow();
//        assertThat(entity.getBalance()).isEqualTo(BigDecimal.valueOf(50.00));
//        assertThat(entity.getUserId()).isEqualTo(userId);
//    }
//
//    @Test
//    void shouldReturnNotFoundWhenRetrievingNonExistentWalletBalance() {
//        UUID nonExistentWalletId = UUID.randomUUID();
//
//        ResponseEntity<String> response = restTemplate.getForEntity(
//                baseUrl + "/" + nonExistentWalletId + "/balance",
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//}