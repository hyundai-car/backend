package com.myme.mycarforme.domains.admin.api;

import com.myme.mycarforme.domains.admin.service.AdminService;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.domains.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // 차량 관리 페이지 관련 엔드포인트 추가
    @GetMapping("/cars")
    public String carsList(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer isOnSale,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Car> carsPage = adminService.getCars(search, isOnSale, pageable);
        model.addAttribute("carsPage", carsPage);
        return "admin/cars";
    }

    @GetMapping("/cars/{id}/detail")
    public String getCarDetail(@PathVariable Long id, Model model) {
        model.addAttribute("car", adminService.getCarDetail(id));
        return "admin/fragments/car-detail :: carDetailModal";
    }

    @GetMapping("/cars/create")
    public String carCreateForm(Model model) {
//        model.addAttribute("car", new CarForm());  // CarForm은 새로 만들어야 할 DTO
        return "admin/car-form";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Long id) {
        adminService.deleteCar(id);
        return "redirect:/admin/cars";
    }
}
