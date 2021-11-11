
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Locale;

public class ConvertWorker {
    public static void main(String[] args) throws IOException {
        ConvertDtoWorker();
        COnvertEnumWorker();
    }

    private static void ConvertDtoWorker() throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\dto\\forguncy\\extension";
        String target = "D:\\newClass.txt";
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFile));
        File files = new File(source);
        File[] fileArr = files.listFiles();

        for (File javaDto : fileArr) {
            if (javaDto.isFile()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(javaDto.getPath()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("package ")) {
                        continue;
                    }
                    if (line.startsWith("import ")) {
                        continue;
                    }
                    if ("@Data".equals(line)) {
                        continue;
                    }
                    if (line.contains("@JsonProperty(")) {
                        continue;
                    }
                    if (line.contains("@JSONField(")) {
                        String propertyName = StringUtils.substring(line, line.indexOf("\""), line.lastIndexOf("\"") + 1);
                        line = "    [JsonProperty(" + propertyName + ")]";
                    }
                    if (line.contains("private ")) {
                        int firstPropertyWord = line.lastIndexOf(" ") + 1;
                        String s = StringUtils.substring(line, firstPropertyWord, firstPropertyWord + 1).toUpperCase();
                        line = line.substring(0, firstPropertyWord) + s + line.substring(firstPropertyWord + 1, line.length() - 1);
                        line += "{ get; set; }";
                    }
                    line = line.replaceAll(" private ", " public ");
                    line = line.replaceAll(" List<", " IEnumerable<");
                    line = line.replaceAll(" String ", " string ");
                    line = line.replaceAll(" boolean ", " bool ");
                    line = line.replaceAll(" Object ", " object ");
                    line = line.replaceAll("<Integer>", "<int>");
                    line = line.replaceAll(" Integer ", " int ");
                    line = line.replaceAll(" extends ", " : ");
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedReader.close();
            }
        }
        bufferedWriter.close();
    }

    private static void COnvertEnumWorker() throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\enumeration\\forguncy";
        String target = "D:\\newEnum.txt";
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFile));
        File files = new File(source);
        File[] fileArr = files.listFiles();

        for (File javaDto : fileArr) {
            if (javaDto.isFile()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(javaDto.getPath()));
                String line;
                boolean isPublicStart = false;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("package ")) {
                        continue;
                    }
                    if (line.startsWith("import ")) {
                        continue;
                    }
                    if (line.startsWith("public enum ")) {
                        isPublicStart = true;
                        line = line.substring(0, line.indexOf(" implements ")) + "{";
                    } else if (line.contains("),") || line.contains(");")) {
                        int leftIndex = line.indexOf("(");
                        String left = line.substring(0, leftIndex);
                        int flag1 = line.indexOf(",");
                        int flag2 = line.indexOf("),");
                        int flag3 = line.indexOf(");");
                        int rightIndex = Math.min(Math.min(flag1 == -1 ? Integer.MAX_VALUE : flag1,
                                        flag2 == -1 ? Integer.MAX_VALUE : flag2),
                                flag3 == -1 ? Integer.MAX_VALUE : flag3);
                        String right = line.substring(leftIndex + 1, rightIndex);
                        line = left + " = " + right + ",";
                    } else {
                        if (isPublicStart && StringUtils.isNoneEmpty(line.trim())) {
                            break;
                        }
                    }
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedWriter.write("}" + "\r\n");
                bufferedReader.close();
            }
        }
        bufferedWriter.close();
    }
}
