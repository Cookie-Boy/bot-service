package ru.sibsutis.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {
    private String id;
    private Long vkUserId;
    private String firstName;
    private String lastName;
    private String phone;
}
