package com.a205.mafya.db.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "gate_log")
public class GateLog extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
