package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private User user;
    private String title;       //4-32
    private int price;	        //0-10000000
    private String description; //8-64

    //Чтобы не читать зря - поставил FetchType.LAZY
    @Lob
    @Column(columnDefinition = "oid")
    @Basic(fetch=FetchType.LAZY)  //Чтобы не читать зря - FetchType.LAZY
    //@Type(type = "org.hibernate.type.ImageType") вредная аннотация, с ней не работает
    private byte [] image;

    @OneToMany(mappedBy = "ad")
    private List<Comment> commentList;

}