package org.sitoolkit.wt.app.compareevidence;

import org.sitoolkit.wt.domain.evidence.EvidenceDir;

public class BaseEvidenceManagerRunner {

    public static void main(String[] args) {

        EvidenceDir targetDir = EvidenceDir
                .targetEvidenceDir(System.getProperty("evidence.target"));
        BaseEvidenceManager baseEvidenceManager = new BaseEvidenceManager();
        baseEvidenceManager.setBaseEvidence(targetDir);

    }

}
