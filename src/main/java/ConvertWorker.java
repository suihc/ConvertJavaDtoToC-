
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Locale;

public class ConvertWorker {
    public static void main(String[] args) throws IOException {
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
                    line = line.replaceAll("<Integer>","<int>");
                    line = line.replaceAll(" Integer "," int ");
                    line = line.replaceAll(" extends ", " : ");
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedReader.close();
            }
        }
        bufferedWriter.close();
    }
}
