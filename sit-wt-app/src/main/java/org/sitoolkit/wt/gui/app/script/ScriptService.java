package org.sitoolkit.wt.gui.app.script;

import java.io.File;

import org.sitoolkit.wt.gui.domain.script.ScriptProcessClient;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class ScriptService {

    ScriptProcessClient client = new ScriptProcessClient();

    public ConversationProcess page2script(String driverType, String baseUrl,
            File baseDir, ProcessExitCallback callback) {

        ProcessParams params = new ProcessParams();
        params.setDirectory(baseDir);
        params.getExitClallbacks().add(callback);

        return client.page2script(driverType, baseUrl, params);
    }

    public ConversationProcess ope2script(String url) {
        return client.ope2script(url);
    }
}
