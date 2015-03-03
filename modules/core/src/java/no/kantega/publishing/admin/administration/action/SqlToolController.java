package no.kantega.publishing.admin.administration.action;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
@RequestMapping("/admin/tools/sqltool")
public class SqlToolController {
    private static final Logger log = LoggerFactory.getLogger(SqlToolController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String viewForm(){
        return "/admin/tools/sqltool.jsp";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String executeSqlQuery(HttpServletRequest request, @RequestParam String query, Model model){
        SecuritySession securitySession = SecuritySession.getInstance(request);
        StringBuilder lines = new StringBuilder();
        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
            log.info(securitySession.getUser().getId() + " executed: " + query);
            if (isNotBlank(query)) {
                try (Connection c  = dbConnectionFactory.getConnection();
                     PreparedStatement st = c.prepareStatement(query)){
                    String lowerCaseQuery = query.toLowerCase();
                    if(lowerCaseQuery.contains("update") || lowerCaseQuery.contains("insert")|| lowerCaseQuery.contains("delete")){
                        lines.append("number of successful updates: ").append(st.executeUpdate());
                    }else{
                        ResultSet rs = st.executeQuery();
                        ResultSetMetaData mdata = rs.getMetaData();
                        int cols = mdata.getColumnCount();
                        lines.append("<tr>");
                        for (int i = 1; i <= cols; i++) {
                            lines.append("<td><b>");
                            lines.append(mdata.getColumnName(i));
                            lines.append("</b></td>");
                        }
                        lines.append("</tr>");
                        while(rs.next()) {
                            lines.append("<tr>");
                            for (int i = 1; i <= cols; i++) {
                                String obj = rs.getString(i);
                                lines.append("<td>");
                                if (obj == null) obj = "null";
                                lines.append(obj);
                                lines.append("</td>");
                            }
                            lines.append("</tr>");
                        }
                    }
                } catch (Exception e) {
                    lines.append(e.toString());
                }
            }
        }
        model.addAttribute("lines",lines);
        model.addAttribute("query",query);
        return "/admin/tools/sqltool.jsp";
    }
}
