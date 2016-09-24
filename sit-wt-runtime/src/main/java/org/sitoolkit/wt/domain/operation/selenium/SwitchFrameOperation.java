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
package org.sitoolkit.wt.domain.operation.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SwitchFrameOperation extends SeleniumOperation {

    private static List<WebElement> switchFrameHistory = new ArrayList<>();

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        ctx.info("フレームを{}に切り替えます", testStep.getItemName());
        Locator locator = testStep.getLocator();
        if (locator.isNa()) {
            seleniumDriver.switchTo().defaultContent();
            position.setCurrentFrame(null);
            switchFrameHistory.clear();
        } else {
            WebElement frame = findElement(locator);
            seleniumDriver.switchTo().frame(frame);
            position.setCurrentFrame(frame);
            switchFrameHistory.add(frame);
        }
    }

    public List<WebElement> getSwitchFrameHistory() {
        return switchFrameHistory;
    }

}
