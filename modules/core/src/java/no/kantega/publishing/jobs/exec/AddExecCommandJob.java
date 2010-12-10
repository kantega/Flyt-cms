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

package no.kantega.publishing.jobs.exec;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.search.index.IndexManager;
import no.kantega.publishing.search.index.jobs.ExecWhileClosedJob;

public class AddExecCommandJob {

    private IndexManager indexManager;
    private String command;

    public void execute() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(this.getClass().getSimpleName(), "Job is disabled for server type slave", null, null);
            return;
        }

        ExecWhileClosedJob job = new ExecWhileClosedJob();
        job.setCommand(command);
        indexManager.addIndexJob(job);
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setCommand(String command) {
        this.command = command;
    }


}
