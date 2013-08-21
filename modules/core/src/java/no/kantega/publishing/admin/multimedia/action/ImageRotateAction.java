package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.multimedia.ImageEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *  Action to perform image rotation
 */
public class ImageRotateAction extends AbstractEditMultimediaAction {

    private ImageEditor imageEditor;

    @Override
    protected ModelAndView handleGet(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap<>();
        RequestParameters param = new RequestParameters(request, "utf-8");
        int id = param.getInt("id");
        String direction = param.getString("direction");

        // converting params to actual degrees
        int degrees = 0;
        if (direction.equals("ccw"))    degrees = -90;
        if (direction.equals("cw"))     degrees = 90;

        // fetch image data
        MultimediaService mediaService = new MultimediaService(request);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mediaService.streamMultimediaData(mm.getId(), new InputStreamHandler(bos));
        mm.setData(bos.toByteArray());

        // Do rotate
        if (mm.getMimeType().getType().contains("image") && degrees != 0){
            imageEditor.rotateMultimedia(mm, degrees);
        }

        // save and return
        mm.setId(mediaService.setMultimedia(mm));
        return new ModelAndView(new RedirectView("EditMultimedia.action?id="+id), model);
    }

    @Override
    protected ModelAndView handlePost(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap<>();
        return new ModelAndView(new RedirectView("Navigate.action"), model);
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }


}
