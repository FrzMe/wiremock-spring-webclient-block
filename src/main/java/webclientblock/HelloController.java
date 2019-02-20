package webclientblock;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Map handle() {
        return Collections.emptyMap();
    }

    @PostMapping(path = "/helloPost", produces = { "application/json" })
    public Map handlePost() {
        return Collections.emptyMap();
    }
}