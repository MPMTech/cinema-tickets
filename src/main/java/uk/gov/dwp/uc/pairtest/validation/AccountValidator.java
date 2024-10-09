package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class AccountValidator  {

    public boolean validateAccount(Long accountId) throws InvalidPurchaseException {
        if(accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid Account");
        }
        return true;
    }
}
