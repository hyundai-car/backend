package com.myme.mycarforme.domains.test.api;

import com.myme.mycarforme.global.common.response.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/get")
    public CommonResponse<Void> getMethod() {
        return CommonResponse.empty();
    }

    @PostMapping("/post")
    public CommonResponse<Dto> postMethod(
            @RequestParam int id,
            @RequestParam String name
    ) {
        return CommonResponse.from(new Dto(id, name));
    }

    public record Dto(
            int id,
            String carName
    ) {
    }
}