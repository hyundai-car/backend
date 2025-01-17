package com.myme.mycarforme.domains.admin.api;

import com.myme.mycarforme.domains.admin.api.response.NeedDeliveryListResponse;
import com.myme.mycarforme.domains.admin.service.AdminService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/orders/{carId}/reset")
    public CommonResponse<Void> orderStatusReset(
            @PathVariable Long carId
    ) {
        adminService.orderStatusReset(carId);
        return CommonResponse.empty();
    }

    @GetMapping("orders/needDelivery")
    public CommonResponse<NeedDeliveryListResponse> getNeedDeliveryList() {
        return CommonResponse.from(adminService.getNeedDelivery());
    }
}
