package com.ktpm.potatoapi.payment.controller;

import com.ktpm.potatoapi.payment.dto.PaymentRequest;
import com.ktpm.potatoapi.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Payment Controller", description = "APIs for payment")
@CrossOrigin("*")
public class PaymentController {
    PaymentService paymentService;

    @GetMapping("create-payment-url")
    public ResponseEntity<?> pay(HttpServletRequest httpServletRequest, PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(httpServletRequest, paymentRequest));
    }

    @GetMapping("/call-back")
    public void callback(@RequestParam Map<String, String> params,
                         HttpServletResponse response) throws IOException {

        String code = params.get("vnp_ResponseCode");

        String redirectUrl;

        if ("00".equals(code)) {
            // Thành công
            redirectUrl = "https://cnpm-rouge.vercel.app/payment-result?status=success";
        } else {
            // Thất bại
            redirectUrl = "https://cnpm-rouge.vercel.app/payment-result?status=fail&code=" + code;
        }

        response.sendRedirect(redirectUrl);
    }
}
