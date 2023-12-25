package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadXml {
    private static ArrayList<AS_ADDR_OBJ_Object> addressObjects = new ArrayList<>();
    private static ArrayList<AS_ADM_HIERARCHY_Object> hierarchyObjects = new ArrayList<>();
    /* понять различие между == и equals */

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
                addressObjects.add(new AS_ADDR_OBJ_Object(objectId, name, typeName, startDate, endDate, isActual, isActive));
            } else if (qName.equals("ITEM")) {
                String objectId = attributes.getValue("OBJECTID");
                String parentObjectId = attributes.getValue("PARENTOBJID");
                hierarchyObjects.add(new AS_ADM_HIERARCHY_Object(objectId, parentObjectId));
            }
        }
    }

    public static List<String> getAddress(String date, List<String> objectIds) {
        // создание пустого списка
        List<String> address = new ArrayList<>();
        for (AS_ADDR_OBJ_Object obj : addressObjects) {
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

    public static List<String> getAddressChainsById() {
        List<String> addressChains = new ArrayList<>();

        for (AS_ADDR_OBJ_Object obj : addressObjects) {
            if (obj.getTypeName().contains("проезд")) {
                List<String> addressChain = buildAddressChain(obj);
                Collections.reverse(addressChain);  // Обратный порядок
                if (!addressChain.isEmpty()) {
                    addressChains.addAll(addressChain);
                    addressChains.add("\n");
                }
            }
        }

        if (addressChains.isEmpty()) {
            addressChains.add("Ничего не найдено.");
        }

        return addressChains;
    }

    private static List<String> buildAddressChain(AS_ADDR_OBJ_Object obj) {
        List<String> addressChain = new ArrayList<>();
        addressChain.add(obj.getTypeName() + " " + obj.getName());

        String currentObjectId = obj.getObjectId();
        String parentObjectId;

        while ((parentObjectId = getParentObjectId(currentObjectId)) != null) {
            AS_ADDR_OBJ_Object parentObject = findObjectById(parentObjectId);

            if (parentObject != null && parentObject.getIsActual().equals("1") && parentObject.getIsActive().equals("1")) {
                addressChain.add(parentObject.getTypeName() + " " + parentObject.getName());
                currentObjectId = parentObject.getObjectId();
            } else {
                break;
            }
        }

        return addressChain;
    }

    private static String getParentObjectId(String objectId) {
        for (AS_ADM_HIERARCHY_Object hierarchyObject : hierarchyObjects) {
            if (hierarchyObject.getObjectId().equals(objectId) ) {
                return hierarchyObject.getParentObjectId();
            }
        }
        return null;
    }

    private static AS_ADDR_OBJ_Object findObjectById(String objectId) {
        for (AS_ADDR_OBJ_Object obj : addressObjects) {
            if (obj.getObjectId().equals(objectId)) {
                return obj;
            }
        }
        return null;
    }

}

