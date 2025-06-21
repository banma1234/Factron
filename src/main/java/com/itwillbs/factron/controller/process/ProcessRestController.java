package com.itwillbs.factron.controller.process;

import com.itwillbs.factron.service.process.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessRestController {

    private final ProcessService processService;
}
