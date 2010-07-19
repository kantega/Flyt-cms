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

package no.kantega.publishing.common.util;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public class InputStreamHandler {
    OutputStream out = null;

    public InputStreamHandler(OutputStream out) {
        this.out = out;
    }

    public void handleInputStream(InputStream in) throws IOException {
        byte[] buf = new byte[32768];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    public OutputStream getOutputStream() {
        return out;
    }
}
