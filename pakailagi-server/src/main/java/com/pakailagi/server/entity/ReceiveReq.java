package com.pakailagi.server.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "receive_req")
public class ReceiveReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receive_req")
    private Integer idReceiveReq;

    @Column(name = "req_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDate;

    @Column(name = "reason_notes", columnDefinition = "TEXT")
    private String reasonNotes;

    @Column(name = "receive_status")
    private String receiveStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_items", referencedColumnName = "id_items")
    private Item item;

    public Integer getIdReceiveReq() {
        return idReceiveReq;
    }

    public void setIdReceiveReq(Integer idReceiveReq) {
        this.idReceiveReq = idReceiveReq;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public String getReasonNotes() {
        return reasonNotes;
    }

    public void setReasonNotes(String reasonNotes) {
        this.reasonNotes = reasonNotes;
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }    
}