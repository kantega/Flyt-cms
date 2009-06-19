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

function BrowserDetect() {
    var userAgent = navigator.userAgent.toLowerCase();

    this.isIE = false;
    this.isSafari = false;
    this.isOpera = false;
    this.isGecko = false;

    if (userAgent.indexOf("opera") != -1) {
        this.isOpera = true;
    }

    if (userAgent.indexOf("safari") != -1) {
        this.isSafari = true;
    }

    if (userAgent.indexOf("msie") != -1 && !this.isOpera) {
        this.isIE = true;
    }

    if (navigator.product && navigator.product == 'Gecko') {
        this.isGecko= true;
    }

}

var browser = new BrowserDetect();