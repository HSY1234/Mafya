package com.a205.mafya.db.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "user_img")
public class UserImg extends BaseEntity {
    @Column(length = 1000, name = "img_url")
    private String imgUrl;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
