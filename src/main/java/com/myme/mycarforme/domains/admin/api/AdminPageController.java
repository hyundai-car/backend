package com.myme.mycarforme.domains.admin.api;

import com.myme.mycarforme.domains.admin.service.AdminService;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.domains.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {
    private final AdminService adminService;

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/keycloak-admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCars", adminService.getTotalCarCount());
        model.addAttribute("todayOrders", adminService.getTodayOrderCount());
        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) Integer status, Model model) {
        List<Car> orders;
        if (status != null) {
            orders = adminService.getOrdersByStatus(status);
        } else {
            orders = adminService.getAllOrders();
        }
        model.addAttribute("orders", orders);
        return "admin/order";
    }

    @PostMapping("/orders/{carId}/status")
    public String updateOrderStatus(@PathVariable Long carId,
            @RequestParam Integer status) {
        adminService.updateOrderStatus(carId, status);
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{carId}/reset")
    public String resetOrderStatus(@PathVariable Long carId) {
        adminService.orderStatusReset(carId);
        return "redirect:/admin/orders";
    }
}
