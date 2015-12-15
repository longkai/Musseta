package yuejia.liu.musseta.components.web;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityComponent;
import yuejia.liu.musseta.components.ActivityScope;

/**
 * The web component, sounds like the new web standard?
 */
@ActivityScope
@Subcomponent
public interface WebComponent extends ActivityComponent<WebActivity> {
  String param_web_delegate = "param_web_delegate";
}
