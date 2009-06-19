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

package no.kantega.publishing.store.util;

import no.kantega.publishing.store.Cart;
import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.store.impl.DefaultCart;

import javax.servlet.http.HttpSession;

/**
 *
 */
public class CartUtils {

    public static final String KEY = Cart.class.getName();

    public static Cart getCart(StoreProvider store, String name, HttpSession session) {
        String key = KEY + ":" + name;
        Cart cart = (Cart) session.getAttribute(key);
        if(cart == null) {
            cart = store.createCart();
            session.setAttribute(key, cart);
        }
        return cart;
    }
}
