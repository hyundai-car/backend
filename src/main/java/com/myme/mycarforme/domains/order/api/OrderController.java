package com.myme.mycarforme.domains.order.api;

import com.myme.mycarforme.domains.order.api.response.ContractResponse;
import com.myme.mycarforme.domains.order.api.response.OrderStatusResponse;
import com.myme.mycarforme.domains.order.api.response.OrderedCarListResponse;
import com.myme.mycarforme.domains.order.service.OrderService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public CommonResponse<OrderedCarListResponse> getOrderedCarList() {
        String userId = SecurityUtil.getUserId();
        return CommonResponse.from(orderService.getOrderedCarList(userId));
    }

    @GetMapping("/{carId}/status")
    public CommonResponse<OrderStatusResponse> getOrderStatus(@PathVariable Long carId) {
        String userId = SecurityUtil.getUserId();
        return CommonResponse.from(orderService.getOrderStatus(userId, carId));
    }

    @PutMapping("/{carId}")
    public CommonResponse<ContractResponse> updateContractStatus(@PathVariable Long carId) {
        String userId = SecurityUtil.getUserId();
        String userName = SecurityUtil.getUserName();
        String email = SecurityUtil.getUserEmail();
        String phoneNumber = SecurityUtil.getUserPhoneNumber();
        return CommonResponse.from(orderService.updateContractStatus(userId,userName,email,phoneNumber,carId));
    }
}
