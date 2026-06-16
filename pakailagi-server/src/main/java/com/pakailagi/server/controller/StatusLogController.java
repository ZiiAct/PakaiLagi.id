package com.pakailagi.server.controller;

import com.pakailagi.server.entity.StatusLog;
import com.pakailagi.server.service.StatusLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status-log")
public class StatusLogController {

    @Autowired
    private StatusLogService statusLogService;

    @GetMapping
    public List<StatusLog> getAllStatusLogs() {
        return statusLogService.getAllStatusLogs();
    }

    @PostMapping
    public StatusLog addStatusLog(@RequestBody StatusLog statusLog) {
        return statusLogService.addStatusLog(statusLog);
    }
}