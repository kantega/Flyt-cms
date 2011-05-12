package org.kantega.openaksess.plugins.jobexecuter;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.AksessLocaleResolver;
import no.kantega.publishing.spring.RootContext;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ListJobsController extends AdminController {
    /**
     * ListJobsController is used to find all jobs that are scheduled using a quartz scheduler.
     * Both jobs specified in a local projects and jobs specified by OpenAksess in application-jobs.xml
     * are listed. Use the aksess config parameter jobexecuter.jobs to limit which jobs are available.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @return ModelAndView Map containing both currently executing jobs and triggers specifying when they will fire.
     * @throws Exception
     */
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String[] jobs = Aksess.getConfiguration().getStrings("jobexecuter.jobs","all");

        LocaleResolver aksessLocaleResolver = new AksessLocaleResolver();
        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, aksessLocaleResolver);


        Map<String, Object> model = new HashMap<String, Object>();

        String runJob = request.getParameter("runJobName");
        String runGroupName = request.getParameter("runGroupName");

        ApplicationContext rootcontext = RootContext.getInstance();
        List <Scheduler> schedulers = new ArrayList<Scheduler>();
        schedulers.addAll(rootcontext.getBeansOfType(Scheduler.class).values());

        if (StringUtils.isNotEmpty(runJob) && StringUtils.isNotEmpty(runGroupName)) {
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getJobDetail(runJob,runGroupName)!=null) {
                    scheduler.triggerJob(runJob,runGroupName);
                }
            }
            return new ModelAndView(new RedirectView("jobs"));
        }


        List<JobExecutionContext> currentyExecuting = new ArrayList<JobExecutionContext>();

        List<Trigger> triggers = new ArrayList<Trigger>();
        for (Scheduler scheduler : schedulers) {
            for (String group : scheduler.getTriggerGroupNames()) {
                for (String trigger : scheduler.getTriggerNames(group)) {
                    Trigger trig = scheduler.getTrigger(trigger,group);
                    /*
                     If trigger does not have any nextfiretime it is caused by a manual execution of a job.
                     We do not want these triggers included, because they will result in duplicates.
                     */
                    if (trig.getNextFireTime()!=null) {
                        triggers.add(trig);
                    }
                }
            }
            currentyExecuting.addAll(scheduler.getCurrentlyExecutingJobs());
        }
        triggers = filterJobs(triggers, jobs);

        model.put("currentlyExecuting", currentyExecuting);
        model.put("triggers", triggers);

        return new ModelAndView ("org/kantega/openaksess/plugins/jobexecuter/view", model);
    }

    private List<Trigger> filterJobs(List<Trigger> triggers, String[] jobs) {
        boolean allJobs = jobs[0].equals("all");
        if (allJobs) {
            return triggers;
        }
        else {
            ArrayList<Trigger> filtered = new ArrayList<Trigger>();
            for (Trigger t : triggers) {
                for (String s : jobs) {
                    if (t.getJobName().equalsIgnoreCase(s)) {
                        filtered.add(t);
                    }
                }
            }
            return filtered;
        }
    }
}

