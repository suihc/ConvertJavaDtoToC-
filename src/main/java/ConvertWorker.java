
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Locale;

public class ConvertWorker {
    public static void main(String[] args) throws IOException {
        File targetFile = new File(TARGET);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFile));
        bufferedWriter.write("using System;\r\n" +
                "using System.Collections.Generic;\r\n" +
                "using ForguncyDataAccess;\r\n" +
                "using Newtonsoft.Json;\r\n" +
                "\r\n" +
                "namespace ServerDesignerCommon.ProcessEngine.ExtensionModel\r\n" +
                "{" + "\r\n");
        ConvertDtoWorker(bufferedWriter);
        COnvertEnumWorker(bufferedWriter);
        bufferedWriter.write("}");
        bufferedWriter.close();
    }
    private static String TARGET = "D:\\newClass.txt";
    private static void ConvertDtoWorker(BufferedWriter bufferedWriter) throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\dto\\forguncy\\extension";

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
                    line = line.replaceAll(" Object ", " object ");
                    line = line.replaceAll(" Long ", " long ");
                    line = line.replaceAll("<Long>", "<long>");
                    line = line.replaceAll(" Integer ", " int ");
                    line = line.replaceAll("<Integer>", "<int>");
                    line = line.replaceAll(" String ", " string ");
                    line = line.replaceAll("<String>", "<string>");
                    line = line.replaceAll(" Boolean ", " bool ");
                    line = line.replaceAll(" boolean ", " bool ");
                    line = line.replaceAll("<Boolean>", "<bool>");
                    line = line.replaceAll("<boolean>", "<bool>");
                    line = line.replaceAll("<Object>", "<object>");
                    line = line.replaceAll(" extends ", " : ");
                    line = line.replaceAll("public object TableFieldValue", "public BindingInfo TableFieldValue");
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedReader.close();
            }
        }

    }

    private static void COnvertEnumWorker(BufferedWriter bufferedWriter) throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\enumeration\\enumtransform";

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
                    if (line.contains("public enum DepartmentFilterDirection")) {
                        bufferedWriter.write("[Flags]" + "\r\n");
                    }
                    bufferedWriter.write(line + "\r\n");
                    if (line.contains("LowerOrSelf = 16,")) {
                        bufferedWriter.write("        ContainsSelf = Self | HigherOrSelf | LowerOrSelf," + "\r\n");
                        bufferedWriter.write("        ToHigher = Higher | HigherOrSelf," + "\r\n");
                        bufferedWriter.write("        ToLower = Lower | LowerOrSelf," + "\r\n");
                    }
                }
                bufferedWriter.write("}" + "\r\n");
                bufferedReader.close();
            }
        }
    }
}
