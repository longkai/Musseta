package yuejia.liu.musseta.components.home.product;

import yuejia.liu.musseta.BuildConfig;

/**
 * Get token response.
 */
public class Token {
  public String scope;
  public String token_type;
  public long   expires_in;
  public String access_token;

  public static class RequestTokenBody {
    public String client_id = BuildConfig.PRODUCT_HUNT_API_KEY;
    public String client_secret = BuildConfig.PRODUCT_HUNT_API_SETRECT;
    public String grant_type = "client_credentials";
  }
}
