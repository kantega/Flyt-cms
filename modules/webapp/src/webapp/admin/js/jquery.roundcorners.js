/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function ($) {
    $.fn.roundCorners = function () {
        return this.each(function () {
            var html = $(this).html();            
            var pre  = '<div class="roundCorners"><div class="top"><div class="corner"></div></div><div class="body"><div class="left"><div class="right">';
            var post = '</div></div></div><div class="bottom"><div class="corner"></div></div></div>';
            $(this).html(pre + html + post);
        });
    };
})(jQuery);