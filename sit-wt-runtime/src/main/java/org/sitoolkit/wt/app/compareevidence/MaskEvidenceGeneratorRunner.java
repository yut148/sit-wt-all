package org.sitoolkit.wt.app.compareevidence;

import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;

public class MaskEvidenceGeneratorRunner {

    public static void main(String[] args) {

        EvidenceDir targetDir = EvidenceDir
                .targetEvidenceDir(System.getProperty("evidence.target"));

        MaskScreenshotGenerator mask = new MaskScreenshotGenerator();
        mask.generate(targetDir);

        MaskEvidenceGenerator evidence = new MaskEvidenceGenerator();
        evidence.generate(targetDir);

        EvidenceOpener opener = new EvidenceOpener();
        opener.openMaskEvidence(targetDir);

    }
}
