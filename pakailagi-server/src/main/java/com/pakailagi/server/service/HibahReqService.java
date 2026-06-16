package com.pakailagi.server.service;

import com.pakailagi.server.entity.HibahReq;
import com.pakailagi.server.repository.HibahReqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HibahReqService {

    @Autowired
    private HibahReqRepository hibahReqRepository;

    public List<HibahReq> getAllHibahReq() {
        return hibahReqRepository.findAll();
    }

    public HibahReq createHibahReq(HibahReq hibahReq) {
        // Logika bisnis: Otomatis set tanggal saat ini dan status awal PENDING
        hibahReq.setReqDate(new Date());
        hibahReq.setReqStatus("PENDING");
        return hibahReqRepository.save(hibahReq);
    }
}