package lissa.trading.user.service.dto.tinkoff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
    private String accountId;
    private String tariff;
    private boolean premStatus;
}
