package no.kantega.publishing.rating.taglib;

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.RatingService;
import no.kantega.publishing.rating.util.RatingUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import java.util.List;

public class HasRatedTag extends ConditionalTagSupport {

    private static final String RATINGS_ATTRIBUTE = "aksess-rating-ratings";
    private String objectid;
    private String context;

    @SuppressWarnings("unchecked")
    protected boolean condition() throws JspTagException {
        if (objectid == null || context == null) {
            return false;
        }
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        List<Rating> userRatings = (List<Rating>) pageContext.getAttribute(RATINGS_ATTRIBUTE);
        if (userRatings == null) {
            RatingService ratingSerivce = (RatingService) pageContext.getServletContext().getAttribute("ratingService");
            userRatings = ratingSerivce.getRatingsForUser(RatingUtil.getUserId(request));
            //Set the user's ratings in the pageContext. Can be reused if this tag occures multiple times on the page. 
            pageContext.setAttribute(RATINGS_ATTRIBUTE, userRatings);
        }

        if (userRatings == null || userRatings.size() == 0) {
            return false;
        }

        for (Rating rating : userRatings) {
            if (objectid.equals(rating.getObjectId()) && context.equals(rating.getContext())) {
                return true;
            }
        }

        return false;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
