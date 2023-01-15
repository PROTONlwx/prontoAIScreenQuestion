import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static int msInWeek = 1000 * 60 * 60 * 24 * 7;

    public static void main(String[] args) {
        printGitStatus("./");
    }

    public static void printGitStatus(String dir) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "cd " + dir);
        processBuilder.command("bash", "-c", "git branch --show-current && git show -s --format=%ct && git log -1 | grep Author && git status");

        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                String[] lines = output.toString().split("\n");
                System.out.println("active branch: " + lines[0]);
                System.out.println("local changes: " + (!output.toString().contains("Changes not staged for commit:")));
                System.out.println("recent commit: " + (System.currentTimeMillis() / 1000L - Integer.parseInt(lines[1]) < msInWeek));
                System.out.println("blame Rufus: " + lines[2].substring(8, 13).equals("Rufus"));
                System.exit(0);
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
