package org.kantega.openaksess.plugins.jobexecuter;

import com.google.common.base.Predicate;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.spring.AksessLocaleResolver;
import no.kantega.publishing.spring.RootContext;
import org.kantega.jexmec.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.MethodInvokingRunnable;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static java.util.Arrays.asList;

/**
 * Controller that lists all jobs and may execute them.
 */
@Controller
@RequestMapping("/administration/jobs")
public class ListJobsController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PluginManager<OpenAksessPlugin> pluginManager;

    @Autowired
    private SystemConfiguration configuration;

    /**
     * ListJobsController is used to find all jobs that are scheduled using a Springs scheduling.
     * Like <code>@Scheduled</code> and, <code>&lt;task:scheduled-tasks&gt; ... &lt;/task:scheduled-tasks&gt;</code>
     * Both jobs specified in a local projects and jobs specified by OpenAksess in application-jobs.xml
     * are listed. Use the aksess config parameter jobexecuter.jobs to limit which jobs are available.
     *
     * @param request The HTTP request
     * @return ModelAndView Map containing both currently executing jobs and triggers specifying when they will fire.
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listJobs(HttpServletRequest request) throws Exception {
        LocaleResolver aksessLocaleResolver = new AksessLocaleResolver();
        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, aksessLocaleResolver);

        Map<String, Object> model = new HashMap<>();

        ApplicationContext rootcontext = RootContext.getInstance();

        putAnnotationScheduledBeans(model, rootcontext);

        return new ModelAndView("org/kantega/openaksess/plugins/jobexecuter/view", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView executeJob(HttpServletRequest request) throws Exception {
        String runAnnotatedBeanJob = request.getParameter("runAnnotatedBeanClassName");
        String runAnnotatedMethodJob = request.getParameter("runAnnotatedMethodName");
        SecuritySession securitySession = SecuritySession.getInstance(request);
        log.info("{} is triggering job {} {}", securitySession.getUser().getId(), runAnnotatedBeanJob, runAnnotatedMethodJob);

        ApplicationContext rootcontext = RootContext.getInstance();
        executeAnnotatedScheduledJob(runAnnotatedBeanJob, runAnnotatedMethodJob, rootcontext);

        return listJobs(request);
    }

    private void putAnnotationScheduledBeans(Map<String, Object> model, ApplicationContext rootcontext) {
        Collection<ApplicationContext> applicationContexts = getPluginApplicationContexts();
        applicationContexts.add(rootcontext);
        Collection<AnnotatedScheduledJob> scheduledAnnotatedJobs = getScheduledAnnotatedJobs(applicationContexts);

        String[] enabledJobs = configuration.getStrings("jobexecuter.jobs","all");
        if(shouldFilterJobs(enabledJobs)){
            scheduledAnnotatedJobs = filterJobs(scheduledAnnotatedJobs, asList(enabledJobs));
        }

        model.put("annotationScheduledBeans", scheduledAnnotatedJobs);
    }

    private Collection<AnnotatedScheduledJob> filterJobs(Collection<AnnotatedScheduledJob> scheduledAnnotatedJobs, final List<String> enabledJobs) {
        return filter(scheduledAnnotatedJobs, new Predicate<AnnotatedScheduledJob>() {
            @Override
            public boolean apply(AnnotatedScheduledJob annotatedScheduledJob) {
                return enabledJobs.contains(annotatedScheduledJob.toString());
            }
        });
    }

    private boolean shouldFilterJobs(String[] jobs) {
        return !(jobs.length == 0 || jobs[0].equals("all"));
    }

    private void executeAnnotatedScheduledJob(String runAnnotatedBeanJob, String runAnnotatedMethodJob, ApplicationContext rootcontext) {
        Collection<ApplicationContext> applicationContexts = getPluginApplicationContexts();
        applicationContexts.add(rootcontext);
        for(ApplicationContext pluginContext : applicationContexts){
            if(tryToExecuteAnnotatedScheduledJob(runAnnotatedBeanJob, runAnnotatedMethodJob, pluginContext)){
                break;
            }
        }
    }

    /**
     * @return true if the bean is found in this context.
     * If it is the job is run.
     */
    private boolean tryToExecuteAnnotatedScheduledJob(final String runAnnotatedBeanJob, final String runAnnotatedMethodJob, ApplicationContext rootcontext) {
        final Object bean;
        Class<?> targetClass;
        try {
            targetClass = Class.forName(runAnnotatedBeanJob);
            bean = rootcontext.getBean(targetClass);
        } catch (BeansException e) {
            return false;
        } catch (ClassNotFoundException e) {
            log.error("Could not find class", e);
            return false;
        }

        targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (method.getName().equals(runAnnotatedMethodJob)) {
                    MethodInvokingRunnable runnable = new MethodInvokingRunnable();
                    runnable.setTargetObject(bean);
                    runnable.setTargetMethod(method.getName());
                    runnable.setArguments(new Object[0]);
                    try {
                        runnable.prepare();
                        runnable.run();
                    } catch (Exception ex) {
                        throw new IllegalStateException("failed to prepare task", ex);
                    }
                }
            }
        });
        return true;
    }

    private Collection<ApplicationContext> getPluginApplicationContexts(){
        Collection<ApplicationContext> contexts = new ArrayList<>();
        for (OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            // OK, this is a hack, but at least its in a plugin, not in the API
            for (MessageSource messageSource : plugin.getMessageSources()) {
                if (messageSource instanceof ApplicationContext) {
                    ApplicationContext ctx = (ApplicationContext) messageSource;
                    contexts.add(ctx);
                }
            }
        }
        return contexts;
    }

    private Collection<AnnotatedScheduledJob> getScheduledAnnotatedJobs(Collection<ApplicationContext> applicationContexts){
        Collection<AnnotatedScheduledJob> jobs = new HashSet<>();

        for (ApplicationContext applicationContext : applicationContexts) {
            jobs.addAll(getScheduledAnnotatedJobsFromApplicationContext(applicationContext));
            jobs.addAll(getTaskScheduledJobsFromApplicationContext(applicationContext));
        }
        return jobs;
    }

    /**
     * Find beans defined by <task:scheduled-tasks>....</task:scheduled-tasks>
     */
    private Collection<AnnotatedScheduledJob> getTaskScheduledJobsFromApplicationContext(ApplicationContext applicationContext) {
        final Collection<AnnotatedScheduledJob> scheduledBeans = new HashSet<>();
        for(ScheduledMethodRunnable methodRunnable : applicationContext.getBeansOfType(ScheduledMethodRunnable.class).values()){
            scheduledBeans.add(new AnnotatedScheduledJob(methodRunnable.getTarget().getClass().getName(), methodRunnable.getMethod().getName(), null));
        }
        return scheduledBeans;
    }

    /**
     * Find beans annotated with @Scheduled
     */
    private Collection<AnnotatedScheduledJob> getScheduledAnnotatedJobsFromApplicationContext(final ApplicationContext context) {
        final Collection<AnnotatedScheduledJob> scheduledBeans = new HashSet<>();
        Map<String, Object> allBeans = context.getBeansOfType(Object.class);
        for (final Map.Entry<String, Object> bean : allBeans.entrySet()) {
            final Class<?> targetClass = AopUtils.getTargetClass(bean.getValue());
            ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    Scheduled annotation = AnnotationUtils.getAnnotation(method, Scheduled.class);
                    if (annotation != null) {
                        scheduledBeans.add(new AnnotatedScheduledJob(targetClass.getName(), method.getName(), annotation.cron()));
                    }
                }
            });
        }
        return scheduledBeans;
    }

    public class AnnotatedScheduledJob {
        private final String className;
        private final String methodName;
        private final String cron;

        public AnnotatedScheduledJob(String className, String methodName, String cron) {
            this.className = className;
            this.methodName = methodName;
            this.cron = cron;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getCron() {
            return cron;
        }

        @Override
        public String toString() {
            return className + '.' + methodName;
        }
    }
}

