/*
 * Copyright 2009 Kantega AS
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package no.kantega.publishing.rating.service;

import no.kantega.publishing.api.rating.*;
import no.kantega.publishing.rating.dao.RatingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

public class RatingServiceImpl implements RatingService, ApplicationContextAware {

    private RatingDao ratingDao;
    private ScoreCalculator scoreCalculator;
    private ApplicationContext applicationContext;

    @Autowired
    public RatingServiceImpl(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    /**
     * @see no.kantega.publishing.api.rating.RatingService#getRatingsForObject(String, String)
     */
    public List<Rating> getRatingsForObject(String objectId, String context) {
        return ratingDao.getRatingsForObject(objectId, context);
    }

    /**
     * @see no.kantega.publishing.api.rating.RatingService#getRatingsForObjects(java.util.List, String)
     */
    public List<Rating> getRatingsForObjects(List<String> objectIds, String context) {
        return ratingDao.getRatingsForObjects(objectIds, context);
    }


    /**
     * @see no.kantega.publishing.api.rating.RatingService#getRatingsForObject(String, String)
     */
    public void deleteRatingsForObject(String objectId, String context) {
        ratingDao.deleteRatingsForObject(objectId, context);
    }


    /**
     * @see no.kantega.publishing.api.rating.RatingService#saveOrUpdateRating(no.kantega.publishing.api.rating.Rating)
     */
    public void saveOrUpdateRating(Rating rating) {

        ratingDao.saveOrUpdateRating(rating);

        RatingNotification notification = new RatingNotification();

        List<Rating> ratings = getRatingsForObject(rating.getObjectId(), rating.getContext());
        notification.setNumberOfRatings(ratings.size());
        notification.setScore(scoreCalculator.getScoreForRatings(ratings));
        notification.setRating(rating);

        Map ratingNotificationListenerBeans = applicationContext.getBeansOfType(RatingNotificationListener.class);
        if (ratingNotificationListenerBeans != null && ratingNotificationListenerBeans.size() > 0)  {
            for (RatingNotificationListener notificationListener : (Iterable<? extends RatingNotificationListener>) ratingNotificationListenerBeans.values()) {
                notificationListener.newRatingNotification(notification);
            }
        }
    }


    /**
     * @see no.kantega.publishing.api.rating.RatingService#getRatingsForUser(String)
     */
    public List<Rating> getRatingsForUser(String userId) {
        return ratingDao.getRatingsForUser(userId);
    }

    /**
     * @see no.kantega.publishing.api.rating.RatingService#deleteRatingsForUser(String, String, String)
     */
    @Override
    public void deleteRatingsForUser(String userId, String objectId, String context) {
        ratingDao.deleteRatingsForUser(userId, objectId, context);
    }


    /**
     * @see no.kantega.publishing.api.rating.RatingService#getScoreForObject(String, String)
     */
    public float getScoreForObject(String objectId, String context) {
        return scoreCalculator.getScoreForRatings(getRatingsForObject(objectId, context));
    }

    public void setScoreCalculator(ScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
