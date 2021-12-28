package no.kantega.publishing.client.device;

public enum DeviceCategory {
    DESKTOP("desktop"),
    MOBILE("mobile");

    private String deviceCategory;

    DeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public static DeviceCategory valueOfDeviceCategory(String deviceCategoryAsString) {
        for (DeviceCategory category : DeviceCategory.values()) {
            if (category.toString().equalsIgnoreCase(deviceCategoryAsString)) {
                return category;
            }
        }

        return DESKTOP;
    }


    public String toString() {
        return deviceCategory;
    }
}
