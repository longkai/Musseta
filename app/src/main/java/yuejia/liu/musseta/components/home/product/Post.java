package yuejia.liu.musseta.components.home.product;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Post entity.
 */
public class Post {
  public long           category_id;
  public String         day;
  public long           id;
  public String         name;
  public String         product_state;
  public String         tagline;
  public int            comments_count;
  public Date           created_at;
  // drop current_user
  public String         discussion_url;
  public boolean        featured;
  public boolean        maker_inside;
  public List<User>     makers;
  public List<Platform> platforms;
  public String         redirect_url;
  public Screenshot_url screenshot_url;
  public User           user;
  public int            votes_count;

  public static class Screenshot_url {
    @SerializedName("300px")
    public String _300px;
    @SerializedName("850px")
    public String _850px;
  }

  public static class Platform {
    public String name;
  }

  public static class PostsRequestBody {
    public int days_ago;
    /** Alternate parameter for requesting specific days (Format: day=YYYY-MM-DD) */
//    public int day;
  }
}
