package uk.gov.dwp.uc.pairtest.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class AccountValidatorTest {

    private AccountValidator accountValidator = new AccountValidator();

    @Test
    @DisplayName("Should return true for valid accountId")
    public void testIsValidAccount()  {
        boolean isValidAccount = accountValidator.validateAccount(1L);
        Assertions.assertTrue(isValidAccount);
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException when accountId is null or less than or equal to 0")
    public void testIsValidAccount_when_account_id_0() {
        InvalidPurchaseException invalidAccountException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> accountValidator.validateAccount(0L));

    }

  
}