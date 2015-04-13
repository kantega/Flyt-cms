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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * In memory implementation of <code>LoginRestrictor</code> that keeps track of
 * previous login attempts that failed.
 * The number of maximum allowed failed attempts and the timeout after this limit is reached is configurable.
 */
public class PauseLoginRestrictor implements LoginRestrictor {
    private Map<String, Integer> numberOfFailedLogins = new HashMap<>();
    private Map<String, Date> lastLogin = new HashMap<>();

    private int maxAttempts = 5;
    private long timeout = 15*60;

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setTimeoutInSeconds(long timeout) {
        this.timeout = timeout;
    }

    public void registerLoginAttempt(String id, boolean success) {
        if (success) {
            removeId(id);
        } else {
            Integer attempts = numberOfFailedLogins.get(id);
            if (attempts == null) {
                attempts = 0;
            }
            attempts++;
            numberOfFailedLogins.put(id, attempts);
            lastLogin.put(id, new Date());
        }
    }

    public boolean isBlocked(String id) {
        Integer attempts = numberOfFailedLogins.get(id);
        if (attempts == null) {
            return false;
        }

        if (attempts >= maxAttempts) {
            // More than max tries

            Date now  = new Date();
            Date last = lastLogin.get(id);
            // User is unlocked after X seconds
            if (now.getTime() >= last.getTime() + timeout*1000) {
                removeId(id);
                return false;
            }


            return true;
        }

        return false;
    }

    private void removeId(String id) {
        numberOfFailedLogins.remove(id);
        lastLogin.remove(id);
    }
}
