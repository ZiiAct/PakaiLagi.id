package com.pakailagi.server.repository;

import com.pakailagi.server.entity.HibahReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HibahReqRepository extends JpaRepository<HibahReq, Integer> {
}