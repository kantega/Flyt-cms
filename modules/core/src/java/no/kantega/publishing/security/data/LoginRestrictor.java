/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.security.data;

/**
 * Register login attempt by user and keep track of whether the attempt limit is reached.
 */
public interface LoginRestrictor {
    /**
     * Register login attempt.
     * @param id - id of user making login attempt
     * @param success - true if login attempt was successfull, false otherwise.
     */
    public void registerLoginAttempt(String id, boolean success);

    /**
     * Check whether user with given id is temporarily blocked from login in.
     * @param id- id of user making login attempt
     * @return true if maximum number of login attemps has been reached.
     */
    public boolean isBlocked(String id);
}
