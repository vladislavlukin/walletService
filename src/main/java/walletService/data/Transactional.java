package walletService.data;

import lombok.Builder;
import lombok.Data;
import walletService.dto.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Transactional {
    private Account account;
    private UUID transactionNumber;
    private long amount;
    private long balance;
    private TransactionType transactionType;
    private Boolean isBlocked;
    private LocalDateTime transactionDate;
}
