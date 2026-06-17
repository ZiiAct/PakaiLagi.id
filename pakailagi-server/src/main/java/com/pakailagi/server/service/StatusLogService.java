package com.pakailagi.server.service;

import com.pakailagi.server.entity.StatusLog;
import com.pakailagi.server.repository.StatusLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusLogService {

    @Autowired
    private StatusLogRepository statusLogRepository;

    public List<StatusLog> getAllStatusLogs() {
        return statusLogRepository.findAll();
    }

    public StatusLog addStatusLog(StatusLog statusLog) {
        return statusLogRepository.save(statusLog);
    }
}