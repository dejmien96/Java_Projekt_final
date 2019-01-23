import java.io.Serializable;
import java.util.LinkedList;

public class Plansza implements Serializable {
    LinkedList<char[][]> plansze = new LinkedList<>();

    // dodaj podana plansze do listy
    public void dodajPlansze(char[][] plansza) {
        plansze.add(new char[10][10]);
        for(int x = 0; x < plansze.getLast().length; x++) {
            for(int y = 0; y < plansze.getLast()[x].length; y++) {
                plansze.getLast()[x][y] = plansza[x][y];
            }
        }
    }

    // zwraca liste
    public LinkedList<char[][]> getList() {
        return plansze;
    }
}
