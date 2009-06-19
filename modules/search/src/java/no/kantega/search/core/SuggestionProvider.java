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

package no.kantega.search.core;

import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;

import java.io.IOException;
import java.util.List;

/**
 * Date: Jan 12, 2009
 * Time: 1:01:30 PM
 *
 * @author Tarje Killingberg
 */
public interface SuggestionProvider {


    public List<Suggestion> provideSuggestions(SuggestionQuery query) throws IOException;

}
