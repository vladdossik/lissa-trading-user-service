package lissa.trading.user.service.utils;

import lissa.trading.lissa.auth.lib.security.EncryptionService;
import lissa.trading.user.service.service.update.factory.SupportedBrokersEnum;

public class TokenUtils {

    private final static String TINKOFF_TOKEN_STARTS_WITH = "t.";
    private final static int TINKOFF_TOKEN_LENGTH = 88;

    public static SupportedBrokersEnum determineTokenKind(String token) {
        if (token == null || token.isEmpty()) {
            return SupportedBrokersEnum.MOEX;
        } if (token.startsWith(TINKOFF_TOKEN_STARTS_WITH)
                && token.length() == TINKOFF_TOKEN_LENGTH) {
            return SupportedBrokersEnum.TINKOFF;
        }
        return SupportedBrokersEnum.MOEX;
    }

    public static String decryptToken(String token) {
        if (!(token.isEmpty())) {
            return EncryptionService.decrypt(token);
        }
        return "";
    }

}
