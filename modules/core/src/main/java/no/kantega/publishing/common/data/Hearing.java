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

package no.kantega.publishing.common.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Hearing {

    private int id = -1;

    private Date deadLine;

    private int contentVersionId;

    private List<HearingInvitee> invitees = new ArrayList<HearingInvitee>();

    public Hearing() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public int getContentVersionId() {
        return contentVersionId;
    }

    public void setContentVersionId(int contentVersionId) {
        this.contentVersionId = contentVersionId;
    }

    public boolean isActive() {
        return isActive(new Date());
    }

    public boolean isActive(Date date) {
        if(deadLine == null) {
            return true;
        } else {
            Date exp = new Date(deadLine.getTime());
            return exp.getTime() > date.getTime();
        }
    }

    public List<HearingInvitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<HearingInvitee> invitees) {
        this.invitees = invitees;
    }
}
