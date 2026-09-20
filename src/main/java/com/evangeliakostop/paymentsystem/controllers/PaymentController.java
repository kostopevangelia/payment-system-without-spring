package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.common.utils.UniqueIdGenerator;
import com.evangeliakostop.paymentsystem.config.PaymentHttpStatusResolver;
import com.evangeliakostop.paymentsystem.config.framework.dependencyinjection.ApplicationContainer;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payments")
@Slf4j
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PaymentHttpStatusResolver statusResolver;

    ApplicationContainer applicationContainer;

    @Autowired
    public PaymentController(ApplicationContainer applicationContainer,
                             PaymentHttpStatusResolver statusResolver) {

        this.applicationContainer = applicationContainer;
        this.paymentService = applicationContainer.paymentService();
        this.statusResolver = statusResolver;
    }

    @Operation(summary = "Init payment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Intent Created",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"amount must be > 0\",\"timestamp\":\"2025-09-14T20:35:12Z\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Fraud detected / business rule conflict",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":409,\"message\":\"Cannot complete payment: Transaction is marked as fraudulent.\",\"timestamp\":\"2025-09-14T20:35:12Z\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"Something Went Wrong.\",\"timestamp\":\"2025-09-14T20:35:12Z\"}")

                    )
            )
    })
    @PostMapping(value = "/init", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentRequest request) {

        String transactionId = UniqueIdGenerator.generateSecureToken();
        try {
            PaymentResponse paymentResponse = paymentService.initiatePayment(request, transactionId);

            return ResponseEntity.status(statusResolver.resolve(paymentResponse)).body(paymentResponse);

        } catch (Exception e) {
            PaymentResponse response = new PaymentResponse();
            response.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
