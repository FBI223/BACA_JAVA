package uj.wmii.pwj.introduction;

public class QuadraticEquation {

    public double[] findRoots(double a, double b, double c) {

        double delta = b * b - 4 * a * c;

        if (delta < 0) { // jesli nie ma pierwiastkow zwroc pusta tablice
            return new double[0];
        }

        else if (delta == 0) {
            double root = -b / (2 * a);
            return new double[] { root };
        }
        // Dwa pierwiastki
        else {
            double root1 = (-b + Math.sqrt(delta)) / (2 * a);
            double root2 = (-b - Math.sqrt(delta)) / (2 * a);
            return new double[] { root1, root2 };
        }

    }

}

