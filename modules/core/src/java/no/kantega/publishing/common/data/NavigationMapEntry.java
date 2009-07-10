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

import java.util.List;
import java.util.ArrayList;

public abstract class NavigationMapEntry extends BaseObject {
    public int currentId = 0;
    public int parentId = 0;
    public int status = 0;
    public String title = "";

    List children = null;
    protected int depth = 0;
    protected boolean isOpen = false;
    protected boolean isSelected = false;

    public NavigationMapEntry() {
    }

    public void addChild(NavigationMapEntry child) {
        if (children == null) {
            children = new ArrayList();
        }
        children.add(child);
    }

    public List getChildren() {
        return children;
    }

    public abstract String getUrl();

    public String getTitle() {
        return title;
    }

    public int getId() {
        return currentId;
    }

    public int getParentId() {
        return parentId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
