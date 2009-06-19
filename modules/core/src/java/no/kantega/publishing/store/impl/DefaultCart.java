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

package no.kantega.publishing.store.impl;

import no.kantega.publishing.store.Cart;
import no.kantega.publishing.store.Product;
import no.kantega.publishing.store.CartLine;

import java.util.List;
import java.util.ArrayList;

public class DefaultCart implements Cart {

    private List lines = new ArrayList();

    public void addLine(Product product, int quantity) {
        lines.add(new DefaultCartLine(product, quantity));
    }

    public List getLines() {
        return lines;
    }

    public float getSum() {
        float sum = 0;
        for (int i = 0; i < lines.size(); i++) {
            CartLine line = (CartLine) lines.get(i);
            sum += line.getProduct().getPrice() * line.getQuantity();
        }

        return sum;
    }

    public int getSize() {
        int size = 0;
        for (int i = 0; i < lines.size(); i++) {
            CartLine line = (CartLine) lines.get(i);
            size += line.getQuantity();
        }
        return size;
    }

    public void clear() {
        lines.clear();
    }
}
