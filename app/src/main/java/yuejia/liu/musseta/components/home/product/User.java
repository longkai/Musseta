package yuejia.liu.musseta.components.home.product;

import java.util.Date;

/**
 * User entity.
 */
public class User {
  public String    id;
  public String    name;
  public String    headline;
  public Date      created_at;
  public String    username;
  public String    twitter_username;
  public String    website_url;
  public Image_url image_url;
  public String    profile_url;

  public static class Image_url {
    // TODO: 12/9/15 consider other dimen?
    public String original;
  }
}
