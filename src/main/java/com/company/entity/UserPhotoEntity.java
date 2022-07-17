package com.company.entity;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user_photo_entity")
public class UserPhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;
    @Column
    private String fielId;
    @Column
    private String link;
}