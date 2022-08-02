package com.company.api.entity;

import com.company.bot.service.MessageService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name, surname;
    private String username;
    private String password;
}