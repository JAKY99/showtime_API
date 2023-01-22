package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Service.VersionControleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/version")
public class VersionControleController {

    private final VersionControleService versionControleService;

    public VersionControleController(VersionControleService versionControleService) {
        this.versionControleService = versionControleService;
    }

    @GetMapping("/get")
    public String getVersion(@RequestParam String type) {
     return versionControleService.getVersion(type);
    }
    @GetMapping("/set")
    public Boolean addVersion(@RequestParam String type) {
        return versionControleService.addVersion(type);
    }

}
