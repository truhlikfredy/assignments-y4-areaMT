package antonkrug.eu;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helper class to access the external strings from the file
 * 
 * @author  Anton Krug
 * @date    26.9.2016
 * @version 1
 */
public class Messages {
  private static final String BUNDLE_NAME = "antonkrug.eu.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);


  private Messages() {
  }


  /**
   * Will search messages file for given key and return coresponding value, or a
   * error value if key wasn't found.
   * 
   * @return
   */
  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

}
