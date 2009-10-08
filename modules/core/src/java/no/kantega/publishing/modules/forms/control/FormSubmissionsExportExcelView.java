package no.kantega.publishing.modules.forms.control;

import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.modules.forms.dao.FormSubmissionDao;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormValue;

/**
 *
 */
public class FormSubmissionsExportExcelView  extends AbstractExcelView {
    FormSubmissionDao dao;

    protected void buildExcelDocument(Map map, org.apache.poi.hssf.usermodel.HSSFWorkbook hssfWorkbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request);
        int formId = param.getInt("formId");
        if (formId != -1) {

            List<String> fieldNames = dao.getFieldNamesForForm(formId);

            HSSFSheet sheet;
            HSSFRow row;
            HSSFCell cell;

            sheet = hssfWorkbook.createSheet("Export");

            row = sheet.createRow((short)0);

            for (int i = 0; i < fieldNames.size(); i++) {
                String header = fieldNames.get(i);
                cell = row.createCell((short)i);
                cell.setCellValue(header);
            }

            List<FormSubmission> formSubmissions = dao.getFormSubmissionsByFormId(formId);
            for (int i = 0; i < formSubmissions.size(); i++) {
                FormSubmission formSubmission = formSubmissions.get(i);
                row = sheet.createRow((short) i+1);

                // Create a cell and put a value in it.

                Map<String, String> values = new HashMap<String, String>();
                for (FormValue value : formSubmission.getValues()) {                    
                    values.put(value.getName(), value.getValuesAsString());
                }
                for (int j = 0; j < fieldNames.size(); j++) {
                    String fieldName = fieldNames.get(j);
                    String value = values.get(fieldName);
                    if (value == null) {
                        value = "";
                    }
                    cell = row.createCell((short) j);
                    cell.setCellValue(value);
                }
            }
        }
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }
}

