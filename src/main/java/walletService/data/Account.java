package walletService.data;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Account {
    private long id;
    private String fullName;
    private String login;
    private String password;
    private long balanceInCents;
    private UUID uniqueNumber;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private LocalDateTime accountCreationDate;
}

