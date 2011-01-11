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

package no.kantega.commons.datasource;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Constructs a datasource from a byte array
 */
public class ByteArrayDataSource implements DataSource {
    private String filename;
    private byte[] data;
    private String contentType;

    public ByteArrayDataSource(String filename, byte[] data, String contentType) {
        this.filename = filename;
        this.data = data;
        this.contentType = contentType;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("getOutputStream not implemented");
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return filename;
    }
}