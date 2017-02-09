package alanstudio.com.easyshop.live;

import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;

public class Module {

    public static void Register(EasyShoppingApplication application){

        new LiveAccountServices(application);
        new LiveShoppingListService(application);
        new LiveShoppingItemService(application);
        new LiveShareListService(application);
    }
}
