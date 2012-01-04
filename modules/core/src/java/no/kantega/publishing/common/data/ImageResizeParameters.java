package no.kantega.publishing.common.data;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.enums.Cropping;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

public class ImageResizeParameters {

    private int maxHeight;
    private int maxWidth;

    private Cropping cropping;

    public ImageResizeParameters(RequestParameters param) {
        maxWidth  = param.getInt("width");
        maxHeight = param.getInt("height");
        String croppingString = param.getString("cropping");
        cropping   = Cropping.getCroppingAsEnum(StringUtils.isBlank(croppingString) ? "contain" : croppingString);
    }

    @Override
    public String toString() {
        return "" + maxHeight + "-" + maxWidth + "-" + cropping;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Cropping getCropping() {
        return cropping;
    }

    public void setCropping(Cropping cropping) {
        this.cropping = cropping;
    }

    public int getMaxHeight() {

        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public boolean skipResize(){
        return maxHeight == -1 && maxWidth == -1;
    }
}
