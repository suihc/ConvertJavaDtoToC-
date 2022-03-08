
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConvertWorker {
    public static void main(String[] args) throws IOException {
        File targetFile = new File(TARGET);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFile));
        bufferedWriter.write("using System;\r\n" +
                "using System.Collections.Generic;\r\n" +
                "using CommonUtilities;\n" +
                "using Forguncy.Properties;\n" +
                "using ForguncyDataAccess;\r\n" +
                "using Newtonsoft.Json;\r\n" +
                "\r\n" +
                "namespace ServerDesignerCommon.ProcessEngine.ExtensionModel\r\n" +
                "{" + "\r\n");
        ConvertDtoWorker(bufferedWriter);
        ConvertEnumWorker(bufferedWriter);
        bufferedWriter.write("}");
        bufferedWriter.close();
    }

    private static String TARGET = "D:\\newClass.txt";

    private static void ConvertDtoWorker(BufferedWriter bufferedWriter) throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\model\\bpmn";

        File file = new File(source);
        File[] files = file.listFiles();

        String enumsource = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\enumeration\\bpmn";

        File enumfiles = new File(enumsource);
        List<File> enumfileList = Arrays.stream(enumfiles.listFiles()).filter(f -> f.isFile()).collect(Collectors.toList());
        List<File> directoryList = Arrays.stream(enumfiles.listFiles()).filter(f -> f.isDirectory()).collect(Collectors.toList());
        for (File directory : directoryList) {
            enumfileList.addAll(Arrays.stream(directory.listFiles()).collect(Collectors.toList()));
        }
        List<String> collect = enumfileList.stream().map(f -> f.getName().substring(0, f.getName().length() - ".java".length())).collect(Collectors.toList());
        for (File javaDto : files) {
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
                    line = line.replaceAll(" Long ", " long? ");
                    line = line.replaceAll("<Long>", "<long?>");
                    line = line.replaceAll(" Integer ", " int? ");
                    line = line.replaceAll("<Integer>", "<int?>");
                    line = line.replaceAll(" String ", " string ");
                    line = line.replaceAll("<String>", "<string>");
                    line = line.replaceAll(" Boolean ", " bool? ");
                    line = line.replaceAll(" boolean ", " bool ");
                    line = line.replaceAll("<Boolean>", "<bool?>");
                    line = line.replaceAll("<boolean>", "<bool>");
                    line = line.replaceAll("<Object>", "<object>");
                    line = line.replaceAll(" extends ", " : ");
                    for (String enumName : collect) {
                        line = line.replaceAll(" " + enumName + " ", " " + enumName + "? ");
                    }
                    line = line.replaceAll("public object TableFieldValue", "public BindingInfo TableFieldValue");
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedReader.close();
            }
        }

    }

    private static void ConvertEnumWorker(BufferedWriter bufferedWriter) throws IOException {
        String source = "D:\\newBitbucket\\Forguncy\\BpmJava\\bpm-server\\src\\main\\java\\com\\grapecity\\forguncy\\enumeration\\bpmn";

        File files = new File(source);
        List<File> fileList = Arrays.stream(files.listFiles()).filter(f -> f.isFile()).collect(Collectors.toList());
        List<File> directoryList = Arrays.stream(files.listFiles()).filter(f -> f.isDirectory()).collect(Collectors.toList());
        for (File directory : directoryList) {
            fileList.addAll(Arrays.stream(directory.listFiles()).collect(Collectors.toList()));
        }
        for (File javaDto : fileList) {
            if (javaDto.isFile() && !javaDto.getName().equals("TaskActionType.java") && !javaDto.getName().equals("RollbackType.java")) {
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
                    if(line.contains("@ResourceKey(key")){
                        continue;
                    }
                    if(line.contains("//")){
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
        bufferedWriter.write("//TODO charlessui some actions will support in the near future\n" +
                "    public enum TaskActionType\n" +
                "    {\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateCommit))]\n" +
                "        Commit = 100,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateTakeOutgoing))]\n" +
                "        TakeOutgoing = 101,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateTerminate))]\n" +
                "        Terminate = 200,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateRollback))]\n" +
                "        Rollback = 300,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateDispatch))]\n" +
                "        Dispatch = 400,\n" +
                "        /*[SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateTransfer))]\n" +
                "        Transfer = 500,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateSuspend))]\n" +
                "        Suspend = 600,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateActivate))]\n" +
                "        Activate = 601,*/\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateDelete))]\n" +
                "        Delete = 700,\n" +
                "        /*[SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateInform))]\n" +
                "        Inform = 800,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_OperateRead))]\n" +
                "        Read = 801,*/\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_UpdateAssignee))]\n" +
                "        UpdateAssignee = 900,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_UpdateDescription))]\n" +
                "        UpdateDescription = 901,\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_UpdateFormKey))]\n" +
                "        UpdateFormKey = 902,\n" +
                "    }\n");
        bufferedWriter.write("public enum RollbackType\r\n" +
                "    {\r\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_Rollback_ToPrevious))]\r\n" +
                "        ToPrevious = 1,\r\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_Rollback_ToStart))]\r\n" +
                "        ToStart = 2,\r\n" +
                "        [SRDescription(nameof(Resources.CommandName_ProcessTaskCommand_Rollback_BackToFrom))]\r\n" +
                "        BackToFrom = 3,\r\n" +
                "    }\r\n");
    }
}
