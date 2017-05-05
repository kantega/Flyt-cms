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

package no.kantega.publishing.api.mailsubscription;

import no.kantega.publishing.api.content.Language;

public class MailSubscription {

    private int channel = -1;
    private int documenttype = -1;
    private int language = Language.NORWEGIAN_BO;
    private MailSubscriptionInterval interval = MailSubscriptionInterval.daily;
    private String email = null;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getDocumenttype() {
        return documenttype;
    }

    public void setDocumenttype(int documenttype) {
        this.documenttype = documenttype;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public MailSubscriptionInterval getInterval() {
        return interval;
    }

    public void setInterval(MailSubscriptionInterval interval) {
        this.interval = interval;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "MailSubscription{" + "channel=" + channel +
                ", documenttype=" + documenttype +
                ", language=" + language +
                ", interval=" + interval +
                ", email='" + email + '\'' +
                '}';
    }
}
