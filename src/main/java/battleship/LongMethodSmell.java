package battleship;

/** Versao corrigida: o metodo longo foi partido em metodos pequenos (Extract Method). */
public class LongMethodSmell {
    public int demo() {
        int first = calcularPrimeiraParte();
        int second = calcularSegundaParte(first);
        return calcularTerceiraParte(second);
    }

    private int calcularPrimeiraParte() {
        int a = 1;
        int b = 2;
        int c = a + b;
        int d = c * 2;
        int e = d - a;
        int f = e + b;
        int g = f * c;
        int h = g - d;
        int i = h + e;
        return i * 2;
    }

    private int calcularSegundaParte(int entrada) {
        int j = entrada - 3;
        int k = j + 4;
        int l = k * 2;
        int m = l * 3;
        int n = m - j;
        int o = n + k;
        int p = o * 2;
        int q = p - l;
        return q + 1;
    }

    private int calcularTerceiraParte(int entrada) {
        int r = entrada + 5;
        int s = r * 2;
        int t = s - 6;
        int u = t + 7;
        int v = u * 2;
        int w = v - 8;
        return w + 9;
    }
}
