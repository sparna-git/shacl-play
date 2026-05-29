package fr.sparna.rdf.shacl.shaclplay.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("api")
    public String swagger(){
        return "api-doc";
    }
}
