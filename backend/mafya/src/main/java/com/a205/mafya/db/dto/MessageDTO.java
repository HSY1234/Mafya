package com.a205.mafya.db.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDTO {
    String to;
    String content;
}
