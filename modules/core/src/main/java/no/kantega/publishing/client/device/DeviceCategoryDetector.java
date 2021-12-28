package no.kantega.publishing.client.device;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;

import javax.servlet.http.HttpServletRequest;

public class DeviceCategoryDetector {
    private final static String OA_DEVICECATEGORY_COOKIE = "oaDeviceCategory";

    private final DeviceResolver deviceResolver= new LiteDeviceResolver();

    public DeviceCategory getUserAgentDeviceCategory(HttpServletRequest request) {
        DeviceCategory deviceCategory = null;

        if (request.getParameter("device") != null) {
            deviceCategory = DeviceCategory.valueOfDeviceCategory(request.getParameter("device"));
        }

        if (deviceCategory == null) {
            deviceCategory = (DeviceCategory)request.getSession().getAttribute(OA_DEVICECATEGORY_COOKIE);
        }

        if (deviceCategory == null) {
            deviceCategory = getDeviceFromUserAgent(request);
        }

        request.getSession().setAttribute(OA_DEVICECATEGORY_COOKIE, deviceCategory);

        return deviceCategory;
    }

    private DeviceCategory getDeviceFromUserAgent(HttpServletRequest request) {
        Device device = deviceResolver.resolveDevice(request);

        return device.isMobile() ? DeviceCategory.MOBILE : DeviceCategory.DESKTOP;
    }
}
