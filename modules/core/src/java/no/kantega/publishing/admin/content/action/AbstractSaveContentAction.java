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

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Hearing;
import no.kantega.publishing.common.data.HearingInvitee;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.MultipleEditorInstancesException;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public abstract class AbstractSaveContentAction extends HttpServlet {
    private static String SOURCE = "aksess.AbstractSaveContentAction";

    protected Content content = null;
    protected RequestParameters param = null;
    protected ContentManagementService aksessService = null;

    abstract ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException, RegExpSyntaxException;
    abstract String getEditPage();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Content content = null;
        RequestParameters param = null;
        ContentManagementService aksessService = null;

        // Skjemadata sendes i UTF-8, må angis
        param = new RequestParameters(request, "utf-8");

        int status = param.getInt("status");
        String action = param.getString("action");
        boolean isModified = param.getBoolean("isModified");
        String message  = "";

        // Id til siden som redigeres, sjekkes mot sesjon for å forhindre feil
        int currentId = param.getInt("currentId");

        try {
            aksessService = new ContentManagementService(request);

            HttpSession session = request.getSession();
            content = (Content)session.getAttribute("currentContent");
            if (content == null) {
                response.sendRedirect("content.jsp?activetab=previewcontent");
                return;
            }

            if (currentId != content.getId()) {
                throw new MultipleEditorInstancesException();
            }


            /*  Dette flagget er kun en indikasjon til brukeren på om innhold er endret.
            *   Innholdet blir uansett lagret når brukeren ber om det.
            */
            content.setIsModified(isModified);

            // Henter alle parametre
            ValidationErrors errors = saveRequestParameters(content, param, aksessService);
            if (errors.getLength() > 0) {
                // Feil på siden, send bruker tilbake for å rette opp feil
                session.setAttribute("errors", errors);
                session.setAttribute("currentContent", content);
                response.sendRedirect("content.jsp?activetab=" + getEditPage());
            } else {
                session.removeAttribute("errors");
                if (status != -1 && errors.getLength() == 0) {
                    content = aksessService.checkInContent(content, status);
                    if(content.getStatus() == ContentStatus.HEARING) {
                        String changeDescription = content.getChangeDescription();
                        saveHearing(aksessService, content, request);
                    }

                    message = "&statusmessage=";
                    status = content.getStatus();
                    switch (status) {
                        case ContentStatus.DRAFT:
                            message += "draft";
                            break;
                        case ContentStatus.PUBLISHED:
                            message += "published";
                            break;
                        case ContentStatus.WAITING:
                            message += "waiting";
                            break;
                        case ContentStatus.HEARING:
                            message += "hearing";
                            break;
                    }

                    // Går alltid til visningside etter lagring, og husk å oppdater tree
                    action = "previewcontent&updatetree=true";
                }
                session.setAttribute("currentContent", content);
                if (action == null || action.length() == 0) {
                    action = getEditPage();
                }
                response.sendRedirect("content.jsp?activetab=" + action + message);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);

            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }

    private void saveHearing(ContentManagementService service, Content content, HttpServletRequest request) throws SystemException {
        Hearing hearing = (Hearing) request.getSession().getAttribute(SaveHearingAction.HEARING_KEY);
        List invitees  = (List) request.getSession().getAttribute(SaveHearingAction.HEARING_INVITEES_KEY);
        request.getSession().removeAttribute(SaveHearingAction.HEARING_INVITEES_KEY);
        request.getSession().removeAttribute(SaveHearingAction.HEARING_KEY);

        hearing.setContentVersionId(content.getVersionId());

        int hearingId = HearingAO.saveOrUpdate(hearing);

        for (int i = 0; i < invitees.size(); i++) {
            HearingInvitee invitee = (HearingInvitee) invitees.get(i);
            invitee.setHearingId(hearingId);
            HearingAO.saveOrUpdate(invitee);
        }

    }
}