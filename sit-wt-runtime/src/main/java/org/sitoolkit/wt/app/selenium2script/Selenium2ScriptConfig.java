package org.sitoolkit.wt.app.selenium2script;

import javax.annotation.Resource;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import({ BaseConfig.class })
public class Selenium2ScriptConfig {

    @Resource
    Environment env;

    @Bean
    public Selenium2Script getConverter(TestScriptDao dao,
            SeleniumStepConverter seleniumStepConverter) {
        Selenium2Script conv = new Selenium2Script();
        conv.setDao(dao);
        conv.setSeleniumStepConverter(seleniumStepConverter);

        return conv;
    }

    @Bean
    public SeleniumTestStep getSeleniumTestStep() {
        return new SeleniumTestStep();
    }

    @Bean
    public SeleniumStepConverter getSeleniumStepConverter(PropertyManager pm) {
        SeleniumStepConverter conv = new SeleniumStepConverter();
        conv.setSeleniumIdeCommandMap(PropertyUtils.loadAsMap("/selenium2operation", false));
        conv.setScreenshotPattern(pm.getSeleniumScreenshotPattern());

        return conv;
    }

}
