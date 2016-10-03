package org.sitoolkit.wt.app.compareevidence;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceUtils;
import org.sitoolkit.wt.domain.evidence.MaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaskScreenshotGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MaskScreenshotGenerator.class);

    private static final String BASE_EVIDENCE_PATH = "base_evidence";

    private static final String MASK_PREFIX = "mask_";

    public static final String SYSPROP_DRIVER_TYPE = "driver.type";

    private List<String> maskedImageList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        MaskScreenshotGenerator generator = new MaskScreenshotGenerator();
        generator.generate(null, "default");
    }

    /**
     * 指定されたエビデンスディレクトリ以下のスクリーンショットに対してマスク処理を行います。マスク処理に使用するマスク情報ファイルは以下の優先度で有効になります。
     *
     * <ol>
     * <li>基準エビデンスルート/${browser}/mask/*.json
     * <li>${user.home}/Downloads/*.json
     * </ol>
     *
     * マスク処理に使用したマスク情報ファイルは基準エビデンスディレクトリ以下に移動します。
     * マスク処理後のスクリーンショットファイルは、処理前のファイルと同じディレクトリに保存し、ファイル名の先頭に"mask_"を付与します。
     *
     * @param targetDir
     *            マスク処理を行うエビデンスのディレクトリ
     * @return マスク後の画像ファイル
     */
    public List<File> generate(EvidenceDir targetDir) {

        LOG.info("スクリーンショットのマスク処理を行います ", targetDir.getDir());

        EvidenceDir baseDir = EvidenceDir.getBase(targetDir.getBrowser());
        MaskInfo maskInfo = MaskInfo.load(baseDir);

        List<File> maskedFiles = new ArrayList<>();

        for (File evidence : targetDir.getEvidenceFiles()) {

            for (File screenshot : targetDir.getScreenshots(evidence.getName())) {

                if (maskInfo.getMaskInfoAsMap().get(screenshot.getName()) == null) {
                    LOG.info("スクリーンショットに対するマスク情報がありません {}", screenshot.getName());
                    continue;
                }

                maskedFiles.add(mask(screenshot, maskInfo));
            }

        }

        return maskedFiles;
    }

    /**
     * 画像ファイルをマスク情報に従ってマスクします。
     *
     * @param imgFile
     *            マスク対象の画像ファイル
     * @param maskInfo
     *            マスク情報
     * @return マスク後の画像ファイル
     *
     */
    public File mask(File imgFile, MaskInfo maskInfo) {

        List<ElementPosition> posStyles = maskInfo.getMaskInfoAsMap().get(imgFile.getName());

        try {
            BufferedImage bi = ImageIO.read(imgFile);
            Graphics2D g = bi.createGraphics();
            g.setPaint(Color.BLACK);
            String maskedImgName = MASK_PREFIX + imgFile.getName();

            for (ElementPosition elementPosition : posStyles) {

                int x = elementPosition.getX();
                int y = elementPosition.getY();
                int width = elementPosition.getW();
                int height = elementPosition.getH();
                g.fillRect(x, y, width, height);

                File maskedImg = new File(imgFile.getParent(), maskedImgName);
                ImageIO.write(bi, "png", maskedImg);

                LOG.info("マスク済み画像ファイルを生成しました {}", maskedImg.getPath());
            }

        } catch (IOException e) {
            LOG.error("画像ファイルのマスク処理で例外が発生しました", e);
        }

        return null;
    }

    @Deprecated
    public void generate(String targetEvidenceDir, String browser) {

        File targetEvidenceDirObj = EvidenceUtils.targetEvidenceDir(targetEvidenceDir);

        if (targetEvidenceDirObj == null) {
            LOG.info("マスク対象となるエビデンスはありません");
            return;
        }

        List<String> evidenceFileNameList = new ArrayList<>();
        for (String s : targetEvidenceDirObj.list()) {
            if (s.endsWith(".html")) {
                evidenceFileNameList.add(s);
            }
        }

        String baseEvidencePath = EvidenceUtils.concatPath(BASE_EVIDENCE_PATH, browser);
        String baseMaskInfoPath = EvidenceUtils.concatPath(baseEvidencePath, "mask");
        String dlPath = EvidenceUtils.concatPath(System.getProperty("user.home"), "downloads");

        for (String evidenceFileName : evidenceFileNameList) {

            String maskInfoFileName = evidenceFileName + ".json";

            File maskInfoInBaseDir = new File(baseMaskInfoPath, maskInfoFileName);
            File maskInfoInDlDir = new File(dlPath, maskInfoFileName);

            if (maskInfoInBaseDir.exists()) {
                buildMaskEvidence(targetEvidenceDirObj, maskInfoInBaseDir, evidenceFileName);

            } else if (maskInfoInDlDir.exists()) {
                buildMaskEvidence(targetEvidenceDirObj, maskInfoInDlDir, evidenceFileName);

                try {
                    FileUtils
                            .moveFileToDirectory(maskInfoInDlDir,
                                    new File(EvidenceUtils
                                            .concatPath(targetEvidenceDirObj.getPath(), "mask")),
                                    true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                LOG.info("マスク情報ファイル[{}]が存在しません マスク処理をスキップします", maskInfoFileName);
            }
        }

    }

    private void buildMaskEvidence(File evidenceDir, File maskInfoBase, String s) {

        maskScreenshots(evidenceDir.getPath(), maskInfoBase);
        createMaskEvidence(evidenceDir.getPath(), s);

    }

    private void maskScreenshots(String evidencePath, File maskInfoFile) {

        String jsonString;
        try {
            jsonString = FileUtils.readFileToString(maskInfoFile, "UTF-8");
            jsonString = StringUtils.replace(jsonString, "/", "\\/"); // JavaScriptで置換できなかったためここで実施
            JsonReader reader = Json.createReader(new StringReader(jsonString));
            JsonObject jsonObj = reader.readObject();
            reader.close();
            JsonArray jsonArray = jsonObj.getJsonArray("maskInfo");

            for (int i = 0; i < jsonArray.size(); i++) {

                JsonObject obj = jsonArray.getJsonObject(i);
                JsonArray posStyles = obj.getJsonArray("posStyle");

                if (posStyles.size() == 0) {
                    continue;
                }

                File screenshotFile = new File(
                        EvidenceUtils.concatPath(evidencePath, obj.getString("imgSrc")));

                maskOneScreenshot(EvidenceUtils.concatPath(evidencePath, "img"), posStyles,
                        screenshotFile);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void maskOneScreenshot(String imgPath, JsonArray posStyles, File screenshotFile) {

        try {
            BufferedImage bi = ImageIO.read(screenshotFile);
            Graphics2D g = bi.createGraphics();
            g.setPaint(Color.BLACK);
            String maskedScreenshotName = MASK_PREFIX + screenshotFile.getName();

            for (int j = 0; j < posStyles.size(); j++) {
                JsonObject posObj = posStyles.getJsonObject(j);
                int x = posObj.getInt("x");
                int y = posObj.getInt("y");
                int width = posObj.getInt("width");
                int height = posObj.getInt("height");
                g.fillRect(x, y, width, height);

                ImageIO.write(bi, "png",
                        new File(EvidenceUtils.concatPath(imgPath, maskedScreenshotName)));
            }
            LOG.info("スクリーンショットをマスクしましたを生成しました {}", maskedScreenshotName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        maskedImageList.add(screenshotFile.getName());
    }

    private void createMaskEvidence(String evidencePath, String evidenceName) {

        try {
            File targetEvidence = new File(EvidenceUtils.concatPath(evidencePath, evidenceName));
            String htmlString = FileUtils.readFileToString(targetEvidence, "UTF-8");

            for (String s : maskedImageList) {
                htmlString = StringUtils.replace(htmlString, s, MASK_PREFIX + s);
            }

            File outputFile = new File(
                    EvidenceUtils.concatPath(evidencePath, MASK_PREFIX + evidenceName));
            FileUtils.writeStringToFile(outputFile, htmlString, "UTF-8");

            LOG.info("エビデンス[{}]を生成しました。", outputFile.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
