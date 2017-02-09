package alanstudio.com.easyshop.infastructure;


import java.util.HashMap;

public class ServiceResponse {

    private HashMap<String, String> propertyErrors;

    public ServiceResponse() {

        propertyErrors = new HashMap<>();
    }

    public void setPropertyErrors(String property, String errors) {
        propertyErrors.put(property,errors);
    }

    public String getPropertyErrors(String property){
        return propertyErrors.get(property);
    }

    public boolean didSucceed(){
        return (propertyErrors.size()==0);
    }
}
