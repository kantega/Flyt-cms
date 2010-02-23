/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.rating;

import no.kantega.publishing.api.rating.RatingService;
import no.kantega.publishing.event.ContentListenerAdapter;
import no.kantega.publishing.common.data.Content;

public class ContentDeleteRatingListener extends ContentListenerAdapter {
    private RatingService ratingService;

    public void contentDeleted(Content content) {
        String objectId = "" + content.getId();
        ratingService.deleteRatingsForObject(objectId, "content");
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }
}
