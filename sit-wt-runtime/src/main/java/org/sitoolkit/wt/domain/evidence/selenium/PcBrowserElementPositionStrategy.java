package org.sitoolkit.wt.domain.evidence.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import org.sitoolkit.wt.domain.operation.selenium.SwitchFrameOperation;

public class PcBrowserElementPositionStrategy implements ElementPositionStrategy {

    @Override
    public void init(ElementPositionSupport2 eps, WebDriver driver) {
        eps.setBasePosition(getCurrentBasePosition(driver, null));
    }

    @Override
    public Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame) {

        SwitchFrameOperation sfo = new SwitchFrameOperation();
        List<WebElement> switchFrameHis = sfo.getSwitchFrameHistory();

        if (currentFrame == null) {
            return driver.findElement(By.tagName("html")).getLocation();
        } else {
            driver.switchTo().defaultContent();
            Point documentPos = null;
            Point framePos = null;
            if (switchFrameHis.size() == 1) {
                documentPos = driver.findElement(By.tagName("html")).getLocation();
            } else {
                Point nestedIframeBasePoint = driver.findElement(By.tagName("html")).getLocation();
                for (WebElement ele : switchFrameHis) {
                    framePos = ele.getLocation();
                    documentPos = nestedIframeBasePoint;
                    if (ele.equals(currentFrame)) {
                        break;
                    } else {
                        nestedIframeBasePoint = new Point(documentPos.getX() - framePos.getX(),
                                documentPos.getY() - framePos.getY());
                        driver.switchTo().frame(ele);
                    }
                }
            }
            framePos = currentFrame.getLocation();
            driver.switchTo().frame(currentFrame);
            return new Point(documentPos.getX() - framePos.getX(),
                    documentPos.getY() - framePos.getY());
        }
    }

}
