package instrumenter;

import java.io.*;


public class Main {

    final static private String dirPath = System.getProperty("user.dir") + "/src/";

    static void instrument(String packageName) throws NumberFormatException, IOException {
        String outputPath = System.getProperty("user.dir") + "/output/" + packageName + "/";
        (new File(outputPath)).mkdirs();

        File packageFolder = new File(dirPath + packageName);
        for (File file : packageFolder.listFiles()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer outputBuffer = new StringBuffer();

            /*add package bc19*/
            outputBuffer.append("package bc19;" + System.lineSeparator());

            while(true){
                String s = br.readLine();
                if (s != null && !s.startsWith("package") && !s.startsWith("import btcutils")) outputBuffer.append(s + System.lineSeparator());
                if (s == null) break;
            }


            try(FileWriter fw = new FileWriter(outputPath + file.getName(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.print(outputBuffer);
            } catch (IOException e) {
                System.err.println("Error");
            }

        }
    }

    public static void main(String[] args){
        try {
            instrument(args[0]);
        } catch(Exception e){
            System.err.println("Error");
        }
    }

}
