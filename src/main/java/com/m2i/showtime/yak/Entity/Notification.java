package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String message;
    private String severity;
    private String type;
    private String status="unread";
    private Date dateCreated= new Date();
    private Date dateRead;
    public Notification(){
    }
    public Notification(String message , String severity , String type , String status){
        this.message = message;
        this.severity = severity;
        this.type = type;
        this.status = status;
    }
    public Notification(String message , String severity , String type){
        this.message = message;
        this.severity = severity;
        this.type = type;
    }
}
