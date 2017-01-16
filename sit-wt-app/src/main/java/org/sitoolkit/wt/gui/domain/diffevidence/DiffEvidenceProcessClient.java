package org.sitoolkit.wt.gui.domain.diffevidence;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class DiffEvidenceProcessClient {
	
	public ConversationProcess genMaskEvidence(String targetEvidence, ProcessParams params){
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("-Devidence.target=" + targetEvidence);
		command.add("org.sitoolkit.wt.app.compareevidence.MaskEvidenceGeneratorRunner");

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

        return process;
	}

	public ConversationProcess setBaseEvidence(String targetEvidence, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("-Devidence.target=" + targetEvidence);
		command.add("org.sitoolkit.wt.app.compareevidence.BaseEvidenceManagerRunner");

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

        return process;
	}

	public ConversationProcess genDiffEvidence(List<File> selectedFiles, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        
        int cnt = selectedFiles.size();
        if (cnt == 2) {
        	command.add("-Devidence.base=" + selectedFiles.get(0).getPath());
        	command.add("-Devidence.target=" + selectedFiles.get(1).getPath());
		} else if (cnt == 1) {
            command.add("-Devidence.target=" + selectedFiles.get(0).getPath());			
		}
        
		command.add("org.sitoolkit.wt.app.compareevidence.DiffEvidenceGeneratorRunner");

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

        return process;
	}
}
