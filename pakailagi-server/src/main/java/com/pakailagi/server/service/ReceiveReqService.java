package com.pakailagi.server.service;

import com.pakailagi.server.entity.ReceiveReq;
import com.pakailagi.server.repository.ReceiveReqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReceiveReqService {

    @Autowired
    private ReceiveReqRepository receiveReqRepository;

    public List<ReceiveReq> getAllReceiveReq() {
        return receiveReqRepository.findAll();
    }

    public ReceiveReq createReceiveReq(ReceiveReq receiveReq) {
        // Logika bisnis: Otomatis set tanggal saat ini dan status awal PENDING
        receiveReq.setReqDate(new Date());
        receiveReq.setReceiveStatus("PENDING");
        return receiveReqRepository.save(receiveReq);
    }
}