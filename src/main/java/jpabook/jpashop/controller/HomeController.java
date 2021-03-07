package jpabook.jpashop.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@Controller
public class HomeController {


    @RequestMapping("/hello/{name}")
    public ResponseEntity<String> home(@PathVariable String name, WebRequest request)
        throws IOException {

//        Resource resource = new ClassPathResource("test.txt");

        ZoneId zoneId = ZoneId.of("GMT");
        long lastModifiedTimestamp = LocalDateTime.of(2020, 02, 4, 19, 57, 45)
            .atZone(zoneId).toInstant().toEpochMilli();

        if (request.checkNotModified(lastModifiedTimestamp)){
            return ResponseEntity.status(304).build();
        }
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
//            .lastModified(resource.lastModified())
            .body("Hello" + name);
    }

    @RequestMapping("/")
    public String homeAA() {
        log.info("home controller");
        return "home";
    }
}
