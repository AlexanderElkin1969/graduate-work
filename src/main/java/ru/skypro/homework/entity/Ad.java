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
    private String image;       //ссылка на фото (в базе или в файле)

    @OneToMany(mappedBy = "ad")
    private List<Comment> commentList;

}