package com.pakailagi.server.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hibah_req")
public class HibahReq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hibah_req")
    private Integer idHibahReq;

    @Column(name = "req_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDate;

    @Column(name = "req_status")
    private String reqStatus;

    @Column(name = "notes_evaluation", columnDefinition = "TEXT")
    private String notesEvaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_items", referencedColumnName = "id_items")
    private Item item;

    public Integer getIdHibahReq() {
        return idHibahReq;
    }

    public void setIdHibahReq(Integer idHibahReq) {
        this.idHibahReq = idHibahReq;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getNotesEvaluation() {
        return notesEvaluation;
    }

    public void setNotesEvaluation(String notesEvaluation) {
        this.notesEvaluation = notesEvaluation;
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