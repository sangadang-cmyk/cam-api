package tech.sangdang.camapi.modules;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/version")
@RestController
public class VersionController {
    @GetMapping()
    public String getVersion() {
        int version = 2;
        return Integer.toString(version);
    }
}
