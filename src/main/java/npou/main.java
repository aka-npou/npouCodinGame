package npou;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by npou on 02.04.2017.
 */
public class main {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int z = in.nextInt();
        for (int i=0; i<z; i++) {
            int k = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();

            int o =0;
            if (x>=k){
                o=x-y;
                if (o <2) o=2;
            } else if (y>=k) {
                o=y-x;
                if (o<2) o=2;
            } else {
                int kx = k - x;
                int ky = k - y;
                if (kx > ky) o = ky;
                else o = kx;
            }

            System.out.println(o);
        }
    }
}
