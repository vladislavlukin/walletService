package walletService.services.admin;

import lombok.AllArgsConstructor;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.AdminResponse;
import walletService.exceptions.DatabaseException;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import java.util.Collections;
import java.util.List;

/**
 * Реализация интерфейса {@code AdminService} для управления административными операциями.
 */
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AccountRepository accountRepository;
    private final TransactionalRepository transactionalRepository;

    /**
     * Генерирует отчет об активности для указанных аккаунтов.
     *
     * @param accounts Список аккаунтов для аудита.
     * @return Текстовый отчет об активности или "Report not found", если отчет не найден.
     */
    public String getAudit(List<Account> accounts) throws DatabaseException {
        StringBuilder result = new StringBuilder();

        if (accounts == null || accounts.isEmpty()){
            return result.toString();
        }

        for (Account account : accounts){
            String status = (!account.getIsBlocked() && !account.getIsDeleted()) ? "active" : (account.getIsDeleted()) ? "deleted" : "blocked";
            result.append("==================================================").append("\n");
            result.append("User Name: ").append(account.getFullName()).append("\n");
            result.append("Login: ").append(account.getLogin()).append("\n");
            result.append("Status: ").append(status).append("\n");
            result.append("Registration Date: ").append(account.getAccountCreationDate()).append("\n");
            result.append("Account Balance: $").append((double) account.getBalanceInCents() / 100).append("\n");

            result.append("--------------------------------------------------").append("\n");

            List<Transactional> transactionals = transactionalRepository.getTransactionalByAccount(account);

            for (Transactional transactional : transactionals) {
                result.append("Transaction Number: ").append(transactional.getTransactionNumber()).append("\n");
                result.append("Date: ").append(transactional.getTransactionDate()).append("\n");
                result.append("Type: ").append(transactional.getTransactionType()).append("\n");
                result.append("Amount: ").append((double) transactional.getAmount() / 100).append("\n");
                result.append("Balance: ").append((double) transactional.getBalance() / 100).append("\n\n");
            }
            result.append("--------------------------------------------------").append("\n");

            result.append("==================================================").append("\n");
        }
        return result.toString();
    }

    @Override
    public AdminResponse getAdminResponse(String login) throws DatabaseException {
        List<Account> accounts;

        if (login.isEmpty()) {
            accounts = accountRepository.getAccounts();
        } else {
            Account account = accountRepository.getAccountByLogin(login);
            accounts = (account != null) ? Collections.singletonList(account) : null;
        }

        String text = getAudit(accounts);
        boolean result = !text.isEmpty();

        if (!result) {
            text = "Report not found";
        }

        return AdminResponse.builder()
                .text(text)
                .result(result)
                .build();
    }
}
