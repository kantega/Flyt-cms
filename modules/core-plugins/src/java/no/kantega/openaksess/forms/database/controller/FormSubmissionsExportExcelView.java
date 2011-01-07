package no.kantega.openaksess.forms.database.controller;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.openaksess.forms.database.dao.FormSubmissionDao;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.api.forms.service.FormService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FormSubmissionsExportExcelView  extends AbstractExcelView {
    @Autowired
    private FormSubmissionDao dao;
    @Autowired
    private FormService formService;

    protected void buildExcelDocument(Map map, HSSFWorkbook hssfWorkbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int formIds[] = param.getInts("formId");
        if (formIds != null && formIds.length > 0) {
            for (int formId : formIds) {
                Form form = formService.getFormById(formId);

                List<String> fieldNames = dao.getFieldNamesForForm(formId);

                String title = cleanUpTitle(form.getTitle());
                HSSFSheet sheet = hssfWorkbook.createSheet(title);
                HSSFRow row = sheet.createRow((short)0);

                HSSFCell cell = row.createCell((short)0);
                cell.setCellValue("Dato innsendt");
                for (int i = 0; i < fieldNames.size(); i++) {
                    String header = fieldNames.get(i);
                    cell = row.createCell((short)(i+1));
                    cell.setCellValue(header);
                }

                HSSFCellStyle dateCellStyle = hssfWorkbook.createCellStyle();
                short dataFormat = hssfWorkbook.createDataFormat().getFormat("dd.mm.yyyy");
                dateCellStyle.setDataFormat(dataFormat);

                Date dateFrom = param.getDate("datefrom", "dd.MM.yyyy");
                Date dateUntil = param.getDate("dateuntil", "dd.MM.yyyy");

                List<FormSubmission> formSubmissions = dao.getFormSubmissionsByFormId(formId);
                int rowNo = 0;
                for (FormSubmission formSubmission : formSubmissions) {
                    if (isWithinDatePeriod(formSubmission, dateFrom, dateUntil)) {
                        rowNo++;
                        row = sheet.createRow((short) rowNo);

                        Map<String, String> values = new HashMap<String, String>();
                        for (FormValue value : formSubmission.getValues()) {
                            values.put(value.getName(), value.getValuesAsString());
                        }

                        cell = row.createCell((short)0);
                        cell.setCellValue(formSubmission.getSubmissionDate());
                        cell.setCellStyle(dateCellStyle);

                        for (int j = 0; j < fieldNames.size(); j++) {
                            String fieldName = fieldNames.get(j);
                            String value = values.get(fieldName);
                            if (value == null) {
                                value = "";
                            }
                            cell = row.createCell((short) (j+1));
                            cell.setCellValue(value);
                        }
                    }
                }
                sheet.autoSizeColumn((short)0);
            }
        }
    }

    /**
     * Title for sheet cannot be blank, greater than 31 chars, or contain any ot these characters /\*?[].
     * @param title
     * @return,
     */
    private String cleanUpTitle(String title){
        if (title == null || title.trim().length() <  1) return "_";
        String chars = "[?/\\[\\]*\\\\]";
        Pattern pattern = Pattern.compile(chars);
        Matcher m = pattern.matcher(title);
        while (m.find()){
            System.out.println(m.start() + ", " + m .group());
            title = title.replace(m.group(),"_");
        }
        if (title.length() >30){
            return title.substring(0,30);
        }
        return title;

    }

    private boolean isWithinDatePeriod(FormSubmission formSubmission, Date dateFrom, Date dateUntil) {
        if (dateFrom != null) {
            if (formSubmission.getSubmissionDate().getTime() < dateFrom.getTime()) {
                return false;
            }
        }

        if (dateUntil != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(dateUntil);
            cal.add(Calendar.DATE, 1);
            if (formSubmission.getSubmissionDate().getTime() > cal.getTimeInMillis()) {
                return false;
            }
        }

        return true;
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }

    public void setFormService(FormService formService) {
        this.formService = formService;
    }
}
