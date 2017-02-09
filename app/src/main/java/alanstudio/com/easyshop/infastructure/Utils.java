package alanstudio.com.easyshop.infastructure;

public class Utils {

    public static final String FIREBASE_BASE_URL = "https://easyshopping-e09de.firebaseio.com/";
    public static final String FIREBASE_USER_REF = FIREBASE_BASE_URL + "users/";
    public static final String FIREBASE_SHOPPING_LIST_REFERENCE = "userShoppingList/";
    public static final String FIREBASE_SHOPPING_ITEM_REFERENCE = "shoppingListItems/";
    public static final String FIREBASE_FRIEND_REF = "usersFriends/";
    public static final String FIREBASE_SHARE_LIST_REF = "sharedWith/";

    public static final String MY_PREFERENCE = "MY_PREFERENCE";
    public static final String EMAIL = "EMAIL";
    public static final String USERNAME = "USERNAME";

    public static final String LIST_ORDER_PREFERENCE = "LIST_ORDER_PREFERENCE";
    public static final String ORDER_BY_KEY = "orderByPushKey";

    /**
     * Method to remove dots from email address so it can be used in the firebase url.
     * @return
     */
    public static String encodeEmail(String userEmail){
        return userEmail.replace(".",",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",",".");
    }

}
