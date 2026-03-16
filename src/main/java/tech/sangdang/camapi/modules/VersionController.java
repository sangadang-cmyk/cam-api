package tech.sangdang.camapi.modules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Slf4j
@RequestMapping("/version")
@RestController
public class VersionController {
    @GetMapping()
    public String getVersion() {
        int version = 2;
        log.info("version: {}", version);  
        return Integer.toString(version);
    }
    
    @GetMapping("/slow")
    public String getSlowVersion() throws InterruptedException {
        Integer version = 2;
        log.info("version: {}", version);
        Thread.sleep(Duration.ofSeconds(5));
        return version.toString();
    }
}
