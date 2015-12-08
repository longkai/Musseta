package yuejia.liu.musseta.misc;

import java.math.BigInteger;
import java.security.SecureRandom;

/** A simple random string generator which copy from stack overflow */
public final class SessionIdentifierGenerator {
  private SecureRandom random = new SecureRandom();

  public String nextSessionId() {
    return new BigInteger(130, random).toString(32);
  }
}
