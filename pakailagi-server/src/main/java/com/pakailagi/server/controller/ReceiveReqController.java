package com.pakailagi.server.controller;

import com.pakailagi.server.entity.ReceiveReq;
import com.pakailagi.server.service.ReceiveReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receive")
public class ReceiveReqController {

    @Autowired
    private ReceiveReqService receiveReqService;

    @GetMapping
    public List<ReceiveReq> getAllReceiveReq() {
        return receiveReqService.getAllReceiveReq();
    }

    @PostMapping
    public ReceiveReq createReceiveReq(@RequestBody ReceiveReq receiveReq) {
        return receiveReqService.createReceiveReq(receiveReq);
    }
}