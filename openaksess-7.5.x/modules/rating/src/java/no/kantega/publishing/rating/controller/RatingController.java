package no.kantega.publishing.rating.controller;

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.RatingService;
import no.kantega.publishing.rating.util.RatingUtil;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
@RequestMapping("/Rating.action")
public class RatingController {

    @Autowired private RatingService ratingService;
    private View jsonView;

    @RequestMapping(method = RequestMethod.POST)
    public View addRating(
            @RequestParam(value = "rating", required = true) Integer ratingValue,
            @RequestParam(value = "objectId", required = true) String objectId,
            @RequestParam(value = "context", required = true) String context,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model,
            HttpServletRequest request,
            HttpServletResponse response) {

        Rating rating = new Rating();
        rating.setDate(new Date());
        rating.setObjectId(objectId);
        rating.setContext(context);
        rating.setRating(ratingValue);
        rating.setUserid(RatingUtil.getUserId(request));

        if (!hasRated(request, objectId, context)) {
            ratingService.saveOrUpdateRating(rating);
            setRatingCookie(response, objectId, context, String.valueOf(ratingValue));
        }

        if(isNotBlank(redirect)) {
            model.put("rating", ratingValue);
            List<Rating> allRatings = ratingService.getRatingsForObject(objectId, context);
            if (allRatings != null) {
                model.put("totalRatings", allRatings.size());
            }
            return jsonView;
        } else {
            return new RedirectView(redirect);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getRatings(
            @RequestParam(value = "objectId", required = true) String objectId,
            @RequestParam(value = "context", required = true) String context,
            ModelMap model ) {

        List<Rating> allRatings = ratingService.getRatingsForObject(objectId, context);
        model.put("objectId", objectId);
        model.put("ratings", allRatings);
        return new ModelAndView("wall/likeslist", model);
    }

    private boolean hasRated(HttpServletRequest request, String objectId, String context) {
        for (Cookie cookie : request.getCookies()) {
            //The user has already rated if she has a cookie for this object.
            if (cookie.getName().equals(getCookieNameForObject(objectId, context))) {
                return true;
            }
        }
        //If she is logged in she might have rated this object even though she doesn't have a cookie.
        SecuritySession secSession = SecuritySession.getInstance(request);
        if (secSession.isLoggedIn()) {
            List<Rating> ratings = ratingService.getRatingsForObject(objectId, context);
            if (ratings == null || ratings.size() == 0 ) {
                return false;
            }
            for (Rating rating : ratings) {
                if (rating.getUserid().equals(secSession.getUser().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setRatingCookie(HttpServletResponse response, String objectId, String context, String value) {
        response.addCookie(new Cookie(getCookieNameForObject(objectId, context), value));
    }

    private String getCookieNameForObject(String objectId, String context) {
        return "aksess-rating-" + context + "-" + objectId;
    }

    public void setJsonView(View jsonView) {
        this.jsonView = jsonView;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }
}
