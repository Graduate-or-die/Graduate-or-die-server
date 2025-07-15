package server.pome.hello;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;

@RestController
@RequestMapping("/health/check")
@RequiredArgsConstructor
@Tag(name = "HealthCheck", description = "헬스체크 API")
public class HelloController {

  @GetMapping("")
  public BaseResponse<String> testAPI() {
    return BaseResponse.success("Health check");
  }
}
