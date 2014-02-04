package no.kantega.useradmin.controls;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.multimedia.MultimediaUploadHandler;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.profile.Profile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ProfileImageController extends AbstractUserAdminController {

    private int imgPreviewMaxHeight = 200;
    private int imgPreviewMaxWidth = 200;

    private MultimediaUploadHandler multimediaUploadHandler;

    @Override
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultimediaService mms = new MultimediaService(request);
        RequestParameters params = new RequestParameters(request);
        String domain = params.getString("domain");
        String id = params.getString("userId");

        ValidationErrors errors = new ValidationErrors();
        Map<String, Object> model = new HashMap<>();
        model.put("domain", domain);
        model.put("userId", id);
        model.put("errors", errors);
        String userId = domain+":"+id;

        DefaultIdentity identity = new DefaultIdentity();
        identity.setUserId(id);
        identity.setDomain(domain);
        Profile profile = getProfileConfiguration(domain).getProfileManager().getProfileForUser(identity);

        String name = id;
        if (profile.getGivenName() != null || profile.getSurname() != null) {
            if (profile.getGivenName() != null && profile.getSurname() != null) {
                name = profile.getGivenName() + " " + profile.getSurname();
            } else if (profile.getGivenName() != null) {
                name = profile.getGivenName();
            } else if (profile.getSurname() != null) {
                name = profile.getSurname();
            }
        }
        model.put("name", name);

        Multimedia profileImage;

        if (request.getMethod().equalsIgnoreCase("POST")) {
            if (request.getParameter("delete") != null) {
                profileImage = mms.getProfileImageForUser(userId);
                mms.deleteMultimedia(profileImage.getId());
            } else {
                MultipartFile file = params.getFile("profileImage");
                if (file != null) {
                    profileImage = new Multimedia();
                    multimediaUploadHandler.updateMultimediaWithData(profileImage, file.getBytes(), file.getOriginalFilename(), true);
                    if (profileImage.getMimeType().getType().startsWith("image")) {

                        profileImage.setName(name);
                        profileImage.setModifiedBy(SecuritySession.getInstance(request).getUser().getId());
                        profileImage.setProfileImageUserId(userId);
                        mms.setProfileImageForUser(profileImage);

                        return new ModelAndView(new RedirectView("profileimage"), model);

                    } else {
                        errors.add(null, "useradmin.profileimage.upload.illegalfiletype");
                    }
                } else {
                    errors.add(null, "useradmin.profileimage.upload.imagereqired");
                }
            }
        }

        profileImage = mms.getProfileImageForUser(userId);

        if (profileImage != null) {
            profileImage.setHeight(imgPreviewMaxHeight);
            profileImage.setWidth(imgPreviewMaxWidth);
            model.put("profileImage", profileImage);
        }


        return new ModelAndView("profile/profileimage", model);
    }

    public void setImgPreviewMaxHeight(int imgPreviewMaxHeight) {
        this.imgPreviewMaxHeight = imgPreviewMaxHeight;
    }

    public void setImgPreviewMaxWidth(int imgPreviewMaxWidth) {
        this.imgPreviewMaxWidth = imgPreviewMaxWidth;
    }

    public void setMultimediaUploadHandler(MultimediaUploadHandler multimediaUploadHandler) {
        this.multimediaUploadHandler = multimediaUploadHandler;
    }
}
