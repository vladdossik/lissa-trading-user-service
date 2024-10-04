package lissa.trading.user.service.dto.tinkoff.account;

import lissa.trading.user.service.dto.tinkoff.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {
    private List<UserAccount> userAccounts;
}
