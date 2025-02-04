package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.auth.dto.LoginUserInfoDto;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.order.dto.ContractedCarDto;
import com.myme.mycarforme.domains.order.dto.PaymentInfoDto;

public record ContractResponse(
        LoginUserInfoDto customer,
        ContractedCarDto car,
        PaymentInfoDto paymentInfoDto
) {
    public static ContractResponse of(
            String name,
            String email,
            String phoneNumber,
            Car car,
            String paymentType,
            Long paidPrice
    ) {
        return new ContractResponse(
                LoginUserInfoDto.builder()
                        .email(email)
                        .name(name)
                        .phoneNumber(phoneNumber)
                        .build(),
                ContractedCarDto.from(car),
                PaymentInfoDto.builder()
                        .paymentType(paymentType)
                        .paidPrice(paidPrice)
                        .build()
        );
    }
}
