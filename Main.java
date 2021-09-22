package com.company;

public class Main {
    public static void main(String[] args) {
        Sudoku.start();
    }

    public static void consoleSudoku() {
        Sudoku sudoku = new Sudoku(
                "--- 7-- ---" +
                        "1-- --- ---" +
                        "--- 43- 2--" +
                        "--- --- --6" +
                        "--- 5-9 ---" +
                        "--- --- 418" +
                        "--- -81 ---" +
                        "--2 --- -5-" +
                        "-4- --- 3--");
        sudoku.solve();
    }
}
