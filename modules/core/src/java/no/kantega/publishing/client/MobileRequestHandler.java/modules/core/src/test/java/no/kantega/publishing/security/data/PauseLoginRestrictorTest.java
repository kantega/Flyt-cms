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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 11, 2009
 * Time: 3:32:12 PM
 */
public class PauseLoginRestrictorTest {

    @Test
    public void testRegisterLoginAttemptAndIsBlocked() {
        PauseLoginRestrictor loginRestrictor = new PauseLoginRestrictor();
        loginRestrictor.setMaxAttempts(5);
        loginRestrictor.setTimeoutInSeconds(2);

        String bill = "bill";
        String jane = "jane";

        // Attempt 4 logins should work ok
        loginRestrictor.registerLoginAttempt(bill, false);
        loginRestrictor.registerLoginAttempt(bill, false);
        loginRestrictor.registerLoginAttempt(bill, false);
        loginRestrictor.registerLoginAttempt(bill, false);
        assertEquals(false, loginRestrictor.isBlocked("bill"));

        // Should not block bill
        loginRestrictor.registerLoginAttempt(jane, false);
        assertEquals(false, loginRestrictor.isBlocked("bill"));

        // Should block bill
        loginRestrictor.registerLoginAttempt(bill, false);
        assertEquals(true, loginRestrictor.isBlocked(bill));

        loginRestrictor.setTimeoutInSeconds(0);
        
        // Not blocked anymore
        assertEquals(false, loginRestrictor.isBlocked(bill));

    }
}
