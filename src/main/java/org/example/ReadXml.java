package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.List;

public class ReadXml {

    private static ArrayList<Object> objects = new ArrayList<>();


    public static class XMLHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("OBJECT")) {
                String objectId = attributes.getValue("OBJECTID");
                String name = attributes.getValue("NAME");
                String typeName = attributes.getValue("TYPENAME");
                String startDate = attributes.getValue("STARTDATE");
                String endDate = attributes.getValue("ENDDATE");
                String isActual = attributes.getValue("ISACTUAL");
                String isActive = attributes.getValue("ISACTIVE");
                objects.add(new Object(objectId, name, typeName, startDate, endDate, isActual, isActive));
            }
        }
    }

    public static List<String> getAddress(String date, List<String> objectIds) {

        List<String> address = new ArrayList<>();
        for (Object obj : objects) {
            String startDate = obj.getStartDate();
            String endDate = obj.getEndDate();
            if (isDateInRange(startDate, endDate, date) && objectIds.contains(obj.getObjectId())) {
                address.add(obj.getObjectId() + ": " + obj.getTypeName() + " " + obj.getName());
            }
        }
        if (address.isEmpty()) {
            address.add("Ничего не найдено.");
        }
        return address;
    }

    private static boolean isDateInRange(String startDate, String endDate, String targetDate) {
        return startDate.compareTo(targetDate) <= 0 && endDate.compareTo(targetDate) >= 0;
    }

    public static List<String> getAddressById(){
        List<String> getAddresses = new ArrayList<>();

        for (Object obj : objects) {
            if(obj.getTypeName().contains("проезд")){
                getAddresses.add(obj.getTypeName() + " " + obj.getName() + " ");
            }
        }
        if (getAddresses.isEmpty()) {
            getAddresses.add("Ничего не найдено.");
        }
        return getAddresses;
    }
}
