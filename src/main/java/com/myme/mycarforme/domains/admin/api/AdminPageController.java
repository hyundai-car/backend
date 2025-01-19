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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {
    private final AdminService adminService;

    @GetMapping
    public String adminPage() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/keycloak-admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
            @PageableDefault(size = 10, sort = "activityDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        model.addAttribute("totalCars", adminService.getTotalCarCount());
        model.addAttribute("todayOrders", adminService.getTodayOrderCount());
        model.addAttribute("activitiesPage", adminService.getRecentActivities(pageable));
        return "admin/dashboard";
    }
}
