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

package no.kantega.publishing.common.data.enums;

import java.util.HashMap;
import java.util.Map;

public class ContentVisibilityStatus {
    public static final int WAITING = 0;
    public static final int ACTIVE  = 10;
    public static final int ARCHIVED  = 15;
    public static final int EXPIRED = 20;
    private final static Map<Integer, String> names = new HashMap<>();
    static {
        names.put(WAITING, "WAITING");
        names.put(ACTIVE, "ACTIVE");
        names.put(ARCHIVED, "ARCHIVED");
        names.put(EXPIRED, "EXPIRED");
    }

    public static String getName(int status) {
        return names.get(status);
    }
}
