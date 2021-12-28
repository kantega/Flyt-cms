/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function ($) {
    $.fn.shiftSelect = function () {
        var all = this;
        $(this).click(function(event) {
            if(typeof(lastClicked) != 'undefined' && event.shiftKey) {
                var clicked = $(all).index(this);
                var last = $(all).index(lastClicked);
                if (clicked != -1 && last != -1) {
                    $(all).slice(Math.min(clicked, last), Math.max(clicked, last)).attr('checked', lastClicked.checked);
                }
            }
            lastClicked = this;
        });

        return this;
    };
})(jQuery);