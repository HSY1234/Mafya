package com.a205.mafya.db.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "manager", uniqueConstraints = @UniqueConstraint(columnNames={"manager_code"}))
public class Manager extends BaseEntity{

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "manager_code", nullable = false, length = 20)
    private String managerCode;

    @Column(name = "class_code", nullable = false, length = 20)
    private String classCode;

    @Column(name = "password", nullable = true, length = 200)
    private String password;
}
