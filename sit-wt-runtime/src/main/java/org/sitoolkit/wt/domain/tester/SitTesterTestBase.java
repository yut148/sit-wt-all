/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.tester;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.infra.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yuichi.kuwahara
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuntimeConfig.class)
public abstract class SitTesterTestBase {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Rule
    public TestName testName = new TestName();

    protected Tester tester;

    protected String getCurrentCaseNo() {
        return StringUtils.substringAfter(testName.getMethodName(), "test");
    }

    protected void test() {
        test(null);
    }

    protected void test(AfterTest afterTest) {
        test(getCurrentCaseNo(), afterTest);
    }

    protected void test(String caseNo, AfterTest afterTest) {
        TestResult result = tester.operate(caseNo);

        if (afterTest != null) {
            afterTest.callback();
        }

        if (!result.isSuccess()) {
            fail(result.buileReason());
        }
    }

    @Before
    public void setUp() {

        // we must get tester instance from application context instead of field
        // injection
        // because field injection with surefire/failsafe parallel execution
        // doesn't work.
        // https://jira.spring.io/browse/SPR-12421
        tester = ApplicationContextHelper.getBean(Tester.class);

        log.trace("setUp {} {} {}", new Object[] { this, testName.getMethodName(), tester });
        tester.prepare(getTestScriptPath(), getSheetName(), getCurrentCaseNo());

        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.before();
    }

    @After
    public void tearDown() {
        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.after();

        tester.tearDown();
    }

    protected abstract String getTestScriptPath();

    protected abstract String getSheetName();

    protected interface AfterTest {
        void callback();
    }

}
