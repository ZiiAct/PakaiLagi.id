package com.pakailagi.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status_log")
public class StatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_log")
    private Integer idStatusLog;

    @Column(name = "item_progress")
    private String itemProgress;

    @Column(name = "latest_status")
    private String latestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_items", referencedColumnName = "id_items")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receive_req", referencedColumnName = "id_receive_req")
    private ReceiveReq receiveReq;

    public Integer getIdStatusLog() {
        return idStatusLog;
    }

    public void setIdStatusLog(Integer idStatusLog) {
        this.idStatusLog = idStatusLog;
    }

    public String getItemProgress() {
        return itemProgress;
    }

    public void setItemProgress(String itemProgress) {
        this.itemProgress = itemProgress;
    }

    public String getLatestStatus() {
        return latestStatus;
    }

    public void setLatestStatus(String latestStatus) {
        this.latestStatus = latestStatus;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ReceiveReq getReceiveReq() {
        return receiveReq;
    }

    public void setReceiveReq(ReceiveReq receiveReq) {
        this.receiveReq = receiveReq;
    }
}