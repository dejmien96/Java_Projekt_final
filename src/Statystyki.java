import java.io.*;

public class Statystyki {
    int trafienia = 0;
    int pudla = 0;
    String nickname = "";

    public Statystyki(String nickname) {

        FileReader fr = null;
        this.nickname = nickname;

        // otworz folder/plik danego uzytkownika na podstawie nickname-u
        try {
            fr = new FileReader(nickname + "/" + nickname + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fr);

        // przeczytaj i zapisz : trafienia i pudla z pliku uzytkownika
        try {
            trafienia = Integer.valueOf(br.readLine());
            pudla = Integer.valueOf(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // zamknij BufferedReader
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // zamknij fileReader
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void zapiszStatystyki() throws IOException {
        // otworz plik danego uzytkownika na podstawie jego nickname-u
        File file = new File(nickname + "/" + nickname + ".txt");

        // w tym string-u zapisz dane z pliku
        String stareDane = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();

        // przeczytaj caly plik i zapisz go w starychDanych oddzielajac linie lineSeparatorem
        while(line != null) {
            stareDane = stareDane + line + System.lineSeparator();
            line = reader.readLine();
        }

        // przepisz stareDane do nowych splitem gdzie tworzy on tablice
        // Stringow tam gdzie oddziela je lineSeparator
        String[] noweDane = stareDane.split(System.lineSeparator());

        // zmien trafienia i pudla zeby byly prawidlowe w pliku
        noweDane[0] = Integer.toString(trafienia);
        noweDane[1] = Integer.toString(pudla);

        // zapisz noweDane do pliku
        FileWriter writer = new FileWriter(file);
        for(int i = 0; i < noweDane.length; i++) {
            writer.write(noweDane[i] + "\n");
        }

        reader.close();
        writer.close();
    }

    public void trafiony() {
        trafienia++;
    }

    public void pudlo() {
        pudla++;
    }

    public int getTrafienia() {
        return trafienia;
    }

    public int getPudla() {
        return pudla;
    }
}
