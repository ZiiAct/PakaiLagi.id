package com.pakailagi.server.repository;

import com.pakailagi.server.entity.ReceiveReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiveReqRepository extends JpaRepository<ReceiveReq, Integer> {
}