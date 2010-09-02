package no.kantega.publishing.modules.forms.control;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.modules.forms.dao.FormSubmissionDao;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.FormValue;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FormSubmissionsExportExcelView  extends AbstractExcelView {

    private FormSubmissionDao dao;


    protected void buildExcelDocument(Map map, HSSFWorkbook hssfWorkbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int formId = param.getInt("formId");
        if (formId != -1) {

            List<String> fieldNames = dao.getFieldNamesForForm(formId);

            HSSFSheet sheet = hssfWorkbook.createSheet("Export");
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

            List<FormSubmission> formSubmissions = dao.getFormSubmissionsByFormId(formId);
            for (int i = 0; i < formSubmissions.size(); i++) {
                FormSubmission formSubmission = formSubmissions.get(i);
                row = sheet.createRow((short) i+1);

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
            sheet.autoSizeColumn((short)0);
        }
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }

}
