package first;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) throws IOException {

        String[][] optab = new String[18][3];
        String[][] assembler = new String[50][4];
        String[][] mc = new String[50][4];
        HashMap<String,String> look = new HashMap<>();
        File f1 = new File("input.txt");
        if(!f1.exists())
            f1.createNewFile();

        createoptab(optab);
        String reg[][] = createregister();
        String cc[][] = createcc();

        int i = createassemblercode(assembler,optab,reg,cc,f1);
        lc(i-1,assembler,mc);
//        for(int j=0;j<i;++j)
//            System.out.println(mc[j][0]);
        converttomachine(i-1,assembler,mc,optab,reg,cc);
        for(int j=0;j<i-1;++j) {
            for (int m = 0; m < 4; ++m)
                System.out.print(mc[j][m] + " ");
            System.out.println();
        }
    }

    private static void createoptab(String [][]optab) throws  IOException{
        File f2 = new File("mnen.txt");
        FileReader f = new FileReader(f2);
        BufferedReader bb = new BufferedReader(f);
        StringTokenizer st;
        int i=0,j;
        for(String line = bb.readLine(); line != null; line = bb.readLine()) {
            //System.out.println(line);
            j=0;
            st = new StringTokenizer(line, " ");
            String a="",b="";
            while(st.hasMoreTokens()) {
                optab[i][j] = st.nextToken();
                j++;
            }
            i++;

        }
        bb.close();
        //f.close();

    }
    private static String[][] createregister() {

        String reg[][] = {{"AREG","1"},{"BREG","2"},{"CREG","3"},{"DREG","4"}};
        return reg;

    }
    private static String[][] createcc() {

        String reg[][] = {{"LT","1"},{"LE","2"},{"EQ","3"},{"GT","4"},{"GE","5"},{"ANY","6"}};
        return reg;

    }

    private static int findmnemonic(String s,String optab[][]) {

        for(int i=0;i<optab.length;++i) {
            if(optab[i][0].equals(s))
                return i;
        }
        return -1;
    }

    private static int isregister(String s,String reg[][]) {

        for(int i=0;i<reg.length;++i) {
            if(reg[i][0].equals(s))
                return i;
        }
        return -1;
    }

    private static int iscc(String s,String reg[][]) {

        for(int i=0;i<reg.length;++i) {
            if(reg[i][0].equals(s))
                return i;
        }
        return -1;
    }

    private static void lc(int i, String[][] assembler, String[][] mc) {
        int x = Integer.parseInt(assembler[0][3]),j;
        int flag;
        for(j=0;j<i;++j) {
            flag = 0;
            if("START".equals(assembler[j][1]) || "END".equals(assembler[j][1]) || "STOP".equals(assembler[j][1]) || "LTORG".equals(assembler[j][1]) || "END".equals(assembler[j][1]) || "ORIGIN".equals(assembler[j][1])) {
                mc[j][0] = "null";
                flag = 2;
            }
            if("ORIGIN".equals(assembler[j][1])) {
                x = Integer.parseInt(assembler[j][3]);
                mc[j+1][0] = assembler[j][3];
                flag = 1;
            }
            if("DS".equals(assembler[j][1])) {
                x += Integer.parseInt(assembler[j][3]);
                mc[j+1][0] = Integer.toString(x);
                flag = 1;
            }
            if (flag == 0) {
                x += 1;
                mc[j + 1][0] = Integer.toString(x);
            }
            if(flag == 2)
                mc[j+1][0] = Integer.toString(x);
        }
        mc[j][0] = "null";
    }

    private static int createassemblercode(String[][] assembler, String[][] optab, String[][] reg, String[][] cc, File f1) throws IOException{
        FileReader fr = new FileReader(f1);
        BufferedReader br = new BufferedReader(fr);
        StringTokenizer st;
        int i=0,j;

        for(String line = br.readLine();line != null; line=br.readLine()) {
            j=0;
            st = new StringTokenizer(line," ");
            while(st.hasMoreTokens()) {
                String t = st.nextToken();
                int x;
                x = findmnemonic(t,optab);
                if(x != -1)
                    assembler[i][1] = optab[x][0];
                if(j==0 && x == -1)
                    assembler[i][0] = t;
                if(j >= 1){
                    x = isregister(t,reg);
                    int y = iscc(t,cc);
                    if(x != -1 || y != -1)
                        assembler[i][2] = (x != -1)?reg[x][0]:cc[y][0];
                    else {
                        assembler[i][3] = t;
                    }
                }
                j++;
            }
            i++;
        }
        return i;
    }

    private static void converttomachine(int i, String[][] assembler, String[][] mc,String[][] optab,String[][] reg, String[][] cc) {

        for(int j=0;j<i;++j) {
            int t = findmnemonic(assembler[j][1],optab);
            if(t!= -1)
                mc[j][1] = optab[t][2] + "(" + optab[t][1] + ")";
            int x = isregister(assembler[j][2],reg);
            if(x != -1)
                mc[j][2] = reg[x][1];
            int y = iscc(assembler[j][2],cc);
            if(y != -1)
                mc[j][2] = cc[y][1];
            mc[j][3] = assembler[j][3];
        }

    }

}
