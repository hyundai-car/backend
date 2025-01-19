package com.myme.mycarforme.domains.admin.api;

import com.myme.mycarforme.domains.admin.service.AdminService;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.domains.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
