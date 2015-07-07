package org.kantega.openaksess.plugins.database.controller;

import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.api.forms.service.FormService;
import org.apache.poi.ss.usermodel.*;
import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FormSubmissionsExportExcelView extends AbstractXlsxStreamingView {
    @Autowired
    private FormSubmissionDao dao;
    @Autowired
    private FormService formService;

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int formIds[] = ServletRequestUtils.getIntParameters(request, "formId");
        if (formIds != null && formIds.length > 0) {
            for (int formId : formIds) {
                Form form = formService.getFormById(formId);

                List<String> fieldNames = dao.getFieldNamesForForm(formId);

                String title = cleanUpTitle(form.getTitle());
                Sheet sheet = workbook.createSheet(title);
                Row row = sheet.createRow((short)0);

                Cell cell = row.createCell(0);
                cell.setCellValue("Dato innsendt");
                for (int i = 0; i < fieldNames.size(); i++) {
                    String header = fieldNames.get(i);
                    cell = row.createCell((i+1));
                    cell.setCellValue(header);
                }

                CellStyle dateCellStyle = workbook.createCellStyle();
                short dataFormat = workbook.createDataFormat().getFormat("dd.mm.yyyy");
                dateCellStyle.setDataFormat(dataFormat);

                String datefromString = ServletRequestUtils.getStringParameter(request, "datefrom", null);
                String dateuntilString = ServletRequestUtils.getStringParameter(request, "dateuntil");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                LocalDate dateFrom = datefromString == null ? null : LocalDate.parse(datefromString, formatter);
                LocalDate dateUntil = dateuntilString == null ? null : LocalDate.parse(dateuntilString, formatter);

                List<FormSubmission> formSubmissions = dao.getFormSubmissionsByFormId(formId);
                int rowNo = 0;
                for (FormSubmission formSubmission : formSubmissions) {
                    if (isWithinDatePeriod(formSubmission, dateFrom, dateUntil)) {
                        rowNo++;
                        row = sheet.createRow((short) rowNo);

                        Map<String, String> values = new HashMap<>();
                        for (FormValue value : formSubmission.getValues()) {
                            values.put(value.getName(), value.getValuesAsString());
                        }

                        cell = row.createCell(0);
                        cell.setCellValue(formSubmission.getSubmissionDate());
                        cell.setCellStyle(dateCellStyle);

                        for (int j = 0; j < fieldNames.size(); j++) {
                            String fieldName = fieldNames.get(j);
                            String value = values.get(fieldName);
                            if (value == null) {
                                value = "";
                            }
                            cell = row.createCell((j+1));
                            cell.setCellValue(value);
                        }
                    }
                }
                sheet.autoSizeColumn((short)0);
            }
        }
    }

    Pattern illegalCharsPattern = Pattern.compile("[?/\\[\\]*\\\\]");

    /**
     * Title for sheet cannot be blank, greater than 31 chars, or contain any of these characters /\*?[].
     * @param title to clean
     * @return title cut to 31 chars and /\*?[]. removed.
     */
    private String cleanUpTitle(String title){
        if (title == null || title.trim().length() <  1) return "_";
        Matcher m = illegalCharsPattern.matcher(title);
        while (m.find()){
            title = title.replace(m.group(),"_");
        }
        if (title.length() >30){
            return title.substring(0,30);
        }
        return title;

    }

    private boolean isWithinDatePeriod(FormSubmission formSubmission, LocalDate dateFrom, LocalDate dateUntil) {
        LocalDate submissionDate = formSubmission.getSubmissionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (dateFrom != null) {
            if (submissionDate.isAfter(dateFrom)) {
                return false;
            }
        }

        if (dateUntil != null) {
            if (submissionDate.isAfter(dateUntil.plusDays(1))) {
                return false;
            }
        }

        return true;
    }
}
