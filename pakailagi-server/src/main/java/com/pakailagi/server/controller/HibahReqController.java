package com.pakailagi.server.controller;

import com.pakailagi.server.entity.HibahReq;
import com.pakailagi.server.service.HibahReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hibah")
public class HibahReqController {

    @Autowired
    private HibahReqService hibahReqService;

    @GetMapping
    public List<HibahReq> getAllHibahReq() {
        return hibahReqService.getAllHibahReq();
    }

    @PostMapping
    public HibahReq createHibahReq(@RequestBody HibahReq hibahReq) {
        return hibahReqService.createHibahReq(hibahReq);
    }
}