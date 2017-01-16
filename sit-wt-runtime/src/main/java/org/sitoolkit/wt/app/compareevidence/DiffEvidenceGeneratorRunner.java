package org.sitoolkit.wt.app.compareevidence;

import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DiffEvidenceGeneratorRunner {

    public static void main(String[] args) {

        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                DiffEvidenceGeneratorConfig.class);
        DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

        EvidenceDir targetDir = EvidenceDir
                .targetEvidenceDir(System.getProperty("evidence.target"));
        EvidenceDir baseDir = EvidenceDir.baseEvidenceDir(System.getProperty("evidence.base"),
                targetDir.getBrowser());

        generator.generate(baseDir, targetDir, false);

        EvidenceOpener opener = new EvidenceOpener();
        opener.openCompareEvidence(targetDir);

    }

}
