/*
 * Copyright 2013 Kantega AS
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

import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.boolex.GEventEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.filter.EvaluatorFilter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY


def loggingDirectory = context.getProperty('logdir')

scan("600 seconds")
appender("oarequest", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss dd-MM-yyyy} %-5level %logger{36} - %msg%n"
    }
    file = "${loggingDirectory}/request.log"
    append = true
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${loggingDirectory}/request.%d{yyyy-MM-dd}.log.gz"
        maxHistory = 30
    }
    filter(EvaluatorFilter) {
        evaluator(GEventEvaluator) {
            expression = 'e.loggerName.contains("no.kantega.publishing.client")'
        }
        onMismatch = DENY
        onMatch = ACCEPT
    }
}
appender("oa", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss dd-MM-yyyy} %-5level %logger{36} - %msg%n"
    }
    file = "${loggingDirectory}/aksess.log"
    append = true
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${loggingDirectory}/aksess.%d{yyyy-MM-dd}.log.gz"
        maxHistory = 30
    }
    filter(EvaluatorFilter) {
        evaluator(GEventEvaluator) {
            expression = '!e.loggerName.contains("no.kantega.publishing.client")'
        }
        onMismatch = DENY
        onMatch = ACCEPT
    }
}

appender("oarequest-async", AsyncAppender) {
    appenderRef('oarequest')
}
appender("oa-async", AsyncAppender) {
    appenderRef('oa')
}

root(INFO, ["oarequest-async", "oa-async"])

logger("no.kantega", INFO)
logger("ro.isdc.wro", WARN)
logger("org.springframework", WARN)
