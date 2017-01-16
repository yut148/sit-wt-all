package org.sitoolkit.wt.gui.app.diffevidence;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.sitoolkit.wt.gui.domain.diffevidence.DiffEvidenceProcessClient;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class DiffEvidenceService {

	private String evidenceDirRegex = "^evidence_.*"; 

    DiffEvidenceProcessClient client = new DiffEvidenceProcessClient();

    public ConversationProcess genMaskEvidence(List<File> selectedFiles,
            ProcessExitCallback callback) {
    	
    	if (selectedFiles.size() != 1) {
    		return null;
    	}

    	File targetEvidence = selectedFiles.get(0);
    	if (!Pattern.matches(evidenceDirRegex, targetEvidence.getName())) {
			return null;
		}
    	
        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.genMaskEvidence(targetEvidence.getPath(), params);
    }

	public ConversationProcess setBaseEvidence(List<File> selectedFiles, 
			ProcessExitCallback callback) {
		
    	if (selectedFiles.size() != 1) {
    		return null;
    	}

    	File targetEvidence = selectedFiles.get(0);
    	if (!Pattern.matches(evidenceDirRegex, targetEvidence.getName())) {
			return null;
		}
    	
        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.setBaseEvidence(targetEvidence.getPath(), params);
	}

	public ConversationProcess genDiffEvidence(List<File> selectedFiles, 
			ProcessExitCallback callback) {
		
    	if (selectedFiles.size() > 2) {
    		return null;
    	}
    	
    	for (File file : selectedFiles) {
        	if (!Pattern.matches(evidenceDirRegex, file.getName())) {
    			return null;
    		}
		}
		
        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.genDiffEvidence(selectedFiles, params);
	}

}
