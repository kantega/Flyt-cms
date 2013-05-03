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

import no.kantega.publishing.api.content.ContentStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class NavigationMapEntry extends BaseObject {
    public int currentId = 0;
    public int parentId = 0;
    public ContentStatus status;
    public String title = "";

    List<NavigationMapEntry> children = new ArrayList<>();
    protected int depth = 0;
    protected boolean isOpen = false;
    protected boolean isSelected = false;
    protected boolean hasChildren = false;
    protected boolean isLastChild = false;
    protected boolean isFirstChild = false;

    public void addChild(NavigationMapEntry child) {
        children.add(child);
        hasChildren = true;
    }

    public List<NavigationMapEntry> getChildren() {
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

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isLastChild() {
        return isLastChild;
    }

    public void setLastChild(boolean lastChild) {
        isLastChild = lastChild;
    }

    public boolean isFirstChild() {
        return isFirstChild;
    }

    public void setFirstChild(boolean firstChild) {
        isFirstChild = firstChild;
    }
}
