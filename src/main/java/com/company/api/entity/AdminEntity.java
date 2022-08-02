package com.company.api.entity;

import com.company.bot.entity.BaseEntity;
import com.company.bot.service.MessageService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
public class AdminEntity extends BaseEntity {
    private String name, surname;
    private String username;
    private String password;
}