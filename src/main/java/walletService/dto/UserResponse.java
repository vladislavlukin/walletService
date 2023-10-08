package walletService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private boolean result;
    private String text;
}
