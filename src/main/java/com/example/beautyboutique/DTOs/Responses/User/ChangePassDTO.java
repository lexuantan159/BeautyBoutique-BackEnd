package com.example.beautyboutique.DTOs.Responses.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassDTO {
    private String oldPassword;
    private String newPassword;
}
