package com.tkahrs.retroexchange.Controllers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping(path = "/")
public class RootRestController {

    @GetMapping("/")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public String returnIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
    // get mapping for hateoas
}
