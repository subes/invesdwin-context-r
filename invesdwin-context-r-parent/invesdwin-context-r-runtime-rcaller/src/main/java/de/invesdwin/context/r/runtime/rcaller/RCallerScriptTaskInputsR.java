package de.invesdwin.context.r.runtime.rcaller;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class RCallerScriptTaskInputsR implements IScriptTaskInputs {

    private final RCallerScriptTaskEngineR engine;

    public RCallerScriptTaskInputsR(final RCallerScriptTaskEngineR engine) {
        this.engine = engine;
    }

    @Override
    public RCallerScriptTaskEngineR getEngine() {
        return engine;
    }

    @Override
    public void putString(final String variable, final String value) {
        if (value == null) {
            putExpression(variable, "NA_character_");
        } else {
            engine.unwrap().getRCode().addString(variable, value);
        }
    }

    @Override
    public void putStringVector(final String variable, final String[] value) {
        engine.unwrap().getRCode().addStringArray(variable, replaceNullWithNa(value));
        engine.unwrap().getRCode().addRCode(variable + "[ " + variable + " == \"NA_character_\" ] <- NA_character_");
    }

    @Override
    public void putStringMatrix(final String variable, final String[][] value) {
        final int rows = value.length;
        final int cols = value[0].length;
        final String[] flatMatrix = new String[rows * cols];
        int i = 0;
        for (int row = 0; row < rows; row++) {
            Assertions.checkEquals(value[row].length, cols);
            for (int col = 0; col < cols; col++) {
                flatMatrix[i] = value[row][col];
                i++;
            }
        }
        putStringVector(variable, flatMatrix);
        putExpression(variable, "matrix(" + variable + ", " + rows + ", " + cols + ", TRUE)");
    }

    private String[] replaceNullWithNa(final String[] value) {
        final String[] array = value.clone();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = "NA_character_";
            }
        }
        return array;
    }

    @Override
    public void putDouble(final String variable, final double value) {
        engine.unwrap().getRCode().addDouble(variable, value);
    }

    @Override
    public void putDoubleVector(final String variable, final double[] value) {
        engine.unwrap().getRCode().addDoubleArray(variable, value);
    }

    @Override
    public void putDoubleMatrix(final String variable, final double[][] value) {
        engine.unwrap().getRCode().addDoubleMatrix(variable, value);
    }

    @Override
    public void putInteger(final String variable, final int value) {
        engine.unwrap().getRCode().addInt(variable, value);
        putExpression(variable, "as.integer(" + variable + ")");
    }

    @Override
    public void putIntegerVector(final String variable, final int[] value) {
        engine.unwrap().getRCode().addIntArray(variable, value);
        putExpression(variable, "as.integer(" + variable + ")");
    }

    @Override
    public void putIntegerMatrix(final String variable, final int[][] value) {
        final double[][] matrix = new double[value.length][];
        for (int i = 0; i < matrix.length; i++) {
            final int[] intVector = value[i];
            final double[] vector = new double[intVector.length];
            for (int j = 0; j < vector.length; j++) {
                vector[j] = intVector[j];
            }
            matrix[i] = vector;
        }
        engine.unwrap().getRCode().addDoubleMatrix(variable, matrix);
        putExpression(variable, "array(as.integer(" + variable + "), dim(" + variable + "))");
    }

    @Override
    public void putBoolean(final String variable, final boolean value) {
        engine.unwrap().getRCode().addLogical(variable, value);
    }

    @Override
    public void putBooleanVector(final String variable, final boolean[] value) {
        engine.unwrap().getRCode().addLogicalArray(variable, value);
    }

    @Override
    public void putBooleanMatrix(final String variable, final boolean[][] value) {
        final double[][] matrix = new double[value.length][];
        for (int i = 0; i < matrix.length; i++) {
            final boolean[] boolVector = value[i];
            final double[] vector = new double[boolVector.length];
            for (int j = 0; j < vector.length; j++) {
                final boolean bool = boolVector[j];
                if (bool) {
                    vector[j] = 1D;
                } else {
                    vector[j] = 0D;
                }
            }
            matrix[i] = vector;
        }
        engine.unwrap().getRCode().addDoubleMatrix(variable, matrix);
        putExpression(variable, "array(as.logical(" + variable + "), dim(" + variable + "))");
    }

    @Override
    public void putExpression(final String variable, final String expression) {
        engine.unwrap().getRCode().addRCode(variable + " <- " + expression);
    }

}
