package com.eventmanagement.DTO;



import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID user_id;
    private String user_name;
}
