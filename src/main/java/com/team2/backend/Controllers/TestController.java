package com.team2.backend.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/")
public class TestController {
  @GetMapping("")
  public String helloWorld() {
    return "Hello World";
  }
}
