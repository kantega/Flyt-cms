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

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.RatingNotification;
import no.kantega.publishing.api.rating.RatingNotificationListener;
import no.kantega.publishing.api.rating.ScoreCalculator;
import no.kantega.publishing.rating.dao.RatingDao;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatingServiceImplTest {


    @Test
    public void testGetRatingsForObject() {
        String objectId = "mockId";
        String context = "mockContext";

        Rating rating1 = new Rating();
        rating1.setContext(context);
        rating1.setObjectId(objectId);
        rating1.setRating(1);
        rating1.setUserid("krisel");

        Rating rating2 = new Rating();
        rating2.setContext(context);
        rating2.setObjectId(objectId);
        rating2.setRating(2);
        rating2.setUserid("andska");

        List<Rating> expected = new ArrayList<Rating>();
        expected.add(rating1);
        expected.add(rating2);

        RatingDao ratingDao = mock(RatingDao.class);
        when(ratingDao.getRatingsForObject(objectId, context)).thenReturn(expected);

        List<Rating> actual = new RatingServiceImpl(ratingDao).getRatingsForObject(objectId, context);

        verify(ratingDao).getRatingsForObject(objectId, context);

        assertEquals(expected,actual);
    }


    @Test
    public void testDeleteRatingsForObject() {
        String objectId = "mockId";
        String context = "mockContext";

        RatingDao ratingDao = mock(RatingDao.class);
        new RatingServiceImpl(ratingDao).deleteRatingsForObject(objectId, context);

        verify(ratingDao).deleteRatingsForObject(objectId, context);
    }


    @Test
    public void testSaveOrUpdateRating() {
        Rating rating = new Rating();
        rating.setContext("mockContext");
        rating.setDate(new Date());
        rating.setObjectId("mockId");
        rating.setRating(3);
        rating.setUserid("krisel");

        Rating prevRating1 = new Rating();
        prevRating1.setContext("mockContext");
        prevRating1.setDate(new Date());
        prevRating1.setObjectId("mockId");
        prevRating1.setRating(1);
        prevRating1.setUserid("krisel");

        Rating prevRating2 = new Rating();
        prevRating2.setContext("mockContext");
        prevRating2.setDate(new Date());
        prevRating2.setObjectId("mockId");
        prevRating2.setRating(2);
        prevRating2.setUserid("krisel");

        List<Rating> ratings = new ArrayList<Rating>();
        ratings.add(rating);
        ratings.add(prevRating1);
        ratings.add(prevRating2);

        RatingDao ratingDao = mock(RatingDao.class);
        when(ratingDao.getRatingsForObject(rating.getObjectId(), rating.getContext())).thenReturn(ratings);

        RatingNotificationListener notificationListener1 = mock(RatingNotificationListener.class);
        RatingNotificationListener notificationListener2 = mock(RatingNotificationListener.class);
        Map<String, Object> notificationListeners = new HashMap();
        notificationListeners.put("listener1", notificationListener1);
        notificationListeners.put("listener2", notificationListener2);

        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getBeansOfType(RatingNotificationListener.class)).thenReturn(notificationListeners);

        RatingNotification expectedNotification = new RatingNotification();
        expectedNotification.setScore(3.5f);
        expectedNotification.setNumberOfRatings(3);
        expectedNotification.setRating(rating);

        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
        when(scoreCalculator.getScoreForRatings(ratings)).thenReturn(3.5f);

        RatingServiceImpl ratingService = new RatingServiceImpl(ratingDao);
        ratingService.setApplicationContext(mockContext);
        ratingService.setScoreCalculator(scoreCalculator);
        ratingService.saveOrUpdateRating(rating);

        verify(ratingDao).saveOrUpdateRating(rating);
        verify(scoreCalculator).getScoreForRatings(ratings);
        verify(notificationListener1).newRatingNotification(refEq(expectedNotification));
        verify(notificationListener2).newRatingNotification(refEq(expectedNotification));
    }


    @Test
    public void testGetRatingsForUser() {
        String userId = "krisel";
        String objectId = "mockId";
        String context = "mockContext";

        Rating rating1 = new Rating();
        rating1.setContext(context);
        rating1.setObjectId(objectId);
        rating1.setRating(1);
        rating1.setUserid("krisel");

        Rating rating2 = new Rating();
        rating2.setContext(context);
        rating2.setObjectId(objectId);
        rating2.setRating(2);
        rating2.setUserid("krisel");

        List<Rating> expected = new ArrayList<Rating>();
        expected.add(rating1);
        expected.add(rating2);

        RatingDao ratingDao = mock(RatingDao.class);
        when(ratingDao.getRatingsForUser(userId)).thenReturn(expected);

        List<Rating> actual = new RatingServiceImpl(ratingDao).getRatingsForUser(userId);

        verify(ratingDao).getRatingsForUser(userId);

        assertEquals(expected,actual);
    }


    @Test
    public void testGetScoreForObject() {

        float expected = 3.5f;
        String objectId = "mockId";
        String context = "mockContext";

        Rating rating1 = new Rating();
        rating1.setContext(context);
        rating1.setDate(new Date());
        rating1.setObjectId(objectId);
        rating1.setRating(3);
        rating1.setUserid("krisel");

        Rating rating2 = new Rating();
        rating2.setContext(context);
        rating2.setDate(new Date());
        rating2.setObjectId(objectId);
        rating2.setRating(1);
        rating2.setUserid("krisel");

        Rating rating3 = new Rating();
        rating3.setContext(context);
        rating3.setDate(new Date());
        rating3.setObjectId(objectId);
        rating3.setRating(2);
        rating3.setUserid("krisel");

        List<Rating> ratings = new ArrayList<Rating>();
        ratings.add(rating1);
        ratings.add(rating2);
        ratings.add(rating3);

        RatingDao ratingDao = mock(RatingDao.class);
        when(ratingDao.getRatingsForObject(objectId, context)).thenReturn(ratings);

        ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
        when(scoreCalculator.getScoreForRatings(ratings)).thenReturn(expected);

        RatingServiceImpl ratingService = new RatingServiceImpl(ratingDao);
        ratingService.setScoreCalculator(scoreCalculator);
        float actual = ratingService.getScoreForObject(objectId, context);

        verify(ratingDao).getRatingsForObject(objectId, context);
        verify(scoreCalculator).getScoreForRatings(ratings);

        assertEquals(expected, actual, 0);
    }

}
