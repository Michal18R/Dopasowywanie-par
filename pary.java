/**
 * Created by Hedi on 2019-06-04.
 */


// Gale–Shapley (Davida Gale’a i Lloyda Shapleya) algorytm użyty

import java.util.*;

class Marriage
{
    private static int N;

    // mała klasa wewnętrza przechowujaca preferencje osoby względem innej
    private static class Rank {
        int personId;
        double value;

        Rank(int personId, double value) {
            // id ocenianej osoby
            this.personId = personId;
            // ocena osoby jako potencjalnego partnera
            this.value = value;
        }
    }

    public static void main(String[] args)
    {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Podaj liczbę osob dla kazdej z plci");
        N = myObj.nextInt();

        Random generator2 = new Random();

        // generujemy macierz z ocenami jakie kobiety przyznały facetom
        List<List<Rank>> prevwmn = new LinkedList<>();
        for(int i=0; i<N; i++) {
            // dla każdej z kobiet tworzymy listę ocen dla każdego faceta
            List<Rank> womanPrev = new LinkedList<>();
            for(int j=0; j<N; j++) {
                // zapisujemy ocenę przyznaną przez kobietę facetowi. Co ważne, faceci mają id od N do 2*N-1 dla tego jest j+N
                womanPrev.add(new Rank(j+N, generator2.nextDouble()));
            }
            // tu jest magia - sortujemy listę z ocenami facetów przyznanymi przez tę kobietę wg przyznanej kolejności.
            // Domyślnie Double.compare sortuje od najmniejszej wartości do największej, dlatego znak minus przed by odwrócić działanie
            womanPrev.sort((o1, o2) -> -Double.compare(o1.value, o2.value));
            prevwmn.add(womanPrev);
        }

        // generujemy macierz z ocenami jakie faceci przyznali kobietom
        List<List<Rank>> prevm = new LinkedList<>();
        for(int i=0; i<N; i++) {
            List<Rank> manPrev = new LinkedList<>();
            for(int j=0; j<N; j++) {
                // zapisujemy oceny facetów przyznane kobietom
                manPrev.add(new Rank(j, generator2.nextDouble()));
            }
            manPrev.sort((o1, o2) -> -Double.compare(o1.value, o2.value));
            prevm.add(manPrev);
        }


        int prefer[][] = new int[2*N][N];
        for(int i=0;i<N;i++) {
            List<Rank> womanRank = prevwmn.get(i);
            for(int j=0;j<N;j++) {
                prefer[i][j] = womanRank.get(j).personId;
            }
        }

        for(int i=0;i<N;i++) {
            List<Rank> manRank = prevm.get(i);
            for(int j=0;j<N;j++) {
                prefer[i+N][j] = manRank.get(j).personId;
            }
        }

        long start = System.currentTimeMillis();

        solve(prefer);

        System.out.println(String.format("For size: %d took: %d ms", N, System.currentTimeMillis()-start));
    }

    static void solve(int prefer[][])
    {
        // tablica w której zapisujemy facetów przypisanych do kobiet
        int currentWomenPartner[] = new int[N];

        // tablica zawierające informacje, czy dany facet jest wolny czy nie nie
        boolean freeMen[] = new boolean[N];

        // wpierw każdej kobiecie ustawiamy, że nie ma przypisanego partnera
        Arrays.fill(currentWomenPartner, -1);

        // i ustalamy liczbę dostępnych facetów na N
        int freeMenCount = N;

        // pętelka tak długo, jak są dostępni wolni faceci
        while (freeMenCount > 0)
        {
            // bierzemy pierwszeg wolnego i zapisujemy w zmiennej m
            int m;
            for (m = 0; m < N; m++) {
                if (freeMen[m] == false) {
                    break;
                }
            }

            // przechodzimy przez kobiety wg preferencji faceta m
            for (int i = 0; i < N && !freeMen[m]; i++) {
                int w = prefer[m][i];
                // jeśli aktualnie sprawdzana kobieta jest dostępna, tworzymy parę
                if (currentWomenPartner[w - N] == -1) {
                    currentWomenPartner[w - N] = m;
                    freeMen[m] = true;
                    freeMenCount--;
                } else { //jeśli nie
                    // sprawdzamy jej aktualego partnera
                    int m1 = currentWomenPartner[w - N];

                    // i jeśli preferuje faceta m bardziej niż swojego aktualnego partnera to zamieniamy jej partnera
                    if (whichManWomenPrefers(prefer, w, m, m1) == false) {
                        currentWomenPartner[w - N] = m;
                        freeMen[m] = true;
                        freeMen[m1] = false;
                    }
                }
            }
        }

        // wyświetlamy wynik
        System.out.println("Woman\tMan");
        for (int i = 0; i < N; i++) {
            System.out.println(String.format("%d\t\t%d", i+N, currentWomenPartner[i]));
        }
    }

    // sprawdza czy kobieta w woli mężczyznę m1 niż m
    static boolean whichManWomenPrefers(int prefer[][], int w, int m, int m1) {
        for (int i = 0; i < N; i++) {
            // jeżeli mężczyzna m1 na liście preferencji kobiety w pojawi się wcześniej, niż mężczyzna m, to zwracamy true
            if (prefer[w][i] == m1) {
                return true;
            }
            // wpierw pojawił się mężczyzna m więc zwracamy false
            if (prefer[w][i] == m) {
                return false;
            }
        }
        return false;
    }
}

