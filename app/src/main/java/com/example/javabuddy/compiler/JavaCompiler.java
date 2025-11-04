package com.example.javabuddy.compiler;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaCompiler {
    
    private static final String TAG = "JavaCompiler";
    private Context context;
    
    // Variable storage for the interpreter
    private Map<String, Object> variables;
    private StringBuilder output;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;
    
    public JavaCompiler(Context context) {
        this.context = context;
        this.variables = new HashMap<>();
        this.output = new StringBuilder();
    }
    
    public CompileResult compileAndRun(String sourceCode) {
        try {
            // Reset state
            variables.clear();
            output.setLength(0);
            
            // Validate basic structure
            if (!validateBasicStructure(sourceCode)) {
                return new CompileResult(false, "Error: Invalid Java structure. Must contain 'public class' and 'public static void main'", "");
            }
            
            // Extract main method content
            String mainContent = extractMainContent(sourceCode);
            if (mainContent == null) {
                return new CompileResult(false, "Error: Could not find main method content", "");
            }
            
            // Execute the code
            boolean success = executeCode(mainContent);
            
            if (success) {
                return new CompileResult(true, "Execution successful", output.toString());
            } else {
                return new CompileResult(false, "Runtime error occurred", output.toString());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in compileAndRun", e);
            return new CompileResult(false, "Error: " + e.getMessage(), "");
        }
    }
    
    private boolean validateBasicStructure(String code) {
        return code.contains("public class") && 
               code.contains("public static void main") &&
               code.contains("String[] args") &&
               countBraces(code) >= 0;
    }
    
    private int countBraces(String code) {
        int open = 0, close = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') open++;
            if (c == '}') close++;
        }
        return open - close;
    }
    
    private String extractMainContent(String sourceCode) {
        try {
            // Find main method
            Pattern mainPattern = Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\([^)]*\\)\\s*\\{");
            Matcher matcher = mainPattern.matcher(sourceCode);
            
            if (matcher.find()) {
                int startIndex = matcher.end();
                
                // Find matching closing brace
                int braceCount = 1;
                int endIndex = startIndex;
                
                while (endIndex < sourceCode.length() && braceCount > 0) {
                    char c = sourceCode.charAt(endIndex);
                    if (c == '{') braceCount++;
                    if (c == '}') braceCount--;
                    endIndex++;
                }
                
                if (braceCount == 0) {
                    return sourceCode.substring(startIndex, endIndex - 1).trim();
                }
            }
            
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting main content", e);
            return null;
        }
    }
    
    private boolean executeCode(String mainContent) {
        try {
            // Split into lines and execute
            String[] lines = mainContent.split("\n");
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;
                
                if (!executeLine(line)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            output.append("Runtime Error: ").append(e.getMessage()).append("\n");
            return false;
        }
    }
    
    private boolean executeLine(String line) {
        try {
            // Handle different types of statements
            if (line.contains("System.out.println")) {
                return handlePrintln(line);
            } else if (line.contains("System.out.print")) {
                return handlePrint(line);
            } else if (isVariableDeclaration(line)) {
                return handleVariableDeclaration(line);
            } else if (isVariableAssignment(line)) {
                return handleVariableAssignment(line);
            } else if (line.contains("for")) {
                return handleForLoop(line);
            } else if (line.contains("while")) {
                return handleWhileLoop(line);
            } else if (line.contains("if")) {
                return handleIfStatement(line);
            }
            
            // If we can't handle the line, just ignore it (like comments, braces, etc.)
            return true;
            
        } catch (Exception e) {
            output.append("Error executing line: ").append(line).append(" - ").append(e.getMessage()).append("\n");
            return false;
        }
    }
    
    private boolean handlePrintln(String line) {
        String content = extractPrintContent(line);
        if (content != null) {
            String evaluated = evaluateExpression(content);
            output.append(evaluated).append("\n");
            return true;
        }
        return false;
    }
    
    private boolean handlePrint(String line) {
        String content = extractPrintContent(line);
        if (content != null) {
            String evaluated = evaluateExpression(content);
            output.append(evaluated);
            return true;
        }
        return false;
    }
    
    private String extractPrintContent(String line) {
        Pattern pattern = Pattern.compile("System\\.out\\.print(?:ln)?\\s*\\((.+)\\)\\s*;?");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    private boolean isVariableDeclaration(String line) {
        return line.matches("\\s*(int|double|float|long|String|boolean|char)\\s+\\w+.*");
    }
    
    private boolean handleVariableDeclaration(String line) {
        try {
            // Remove semicolon
            line = line.replace(";", "");
            
            // Parse: type varName = value
            String[] parts = line.split("=");
            String leftPart = parts[0].trim();
            String[] typeParts = leftPart.split("\\s+");
            
            if (typeParts.length >= 2) {
                String type = typeParts[0];
                String varName = typeParts[1];
                
                Object value = null;
                if (parts.length > 1) {
                    String valueStr = parts[1].trim();
                    value = parseValue(type, valueStr);
                }
                
                variables.put(varName, value);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isVariableAssignment(String line) {
        return line.contains("=") && !isVariableDeclaration(line);
    }
    
    private boolean handleVariableAssignment(String line) {
        try {
            line = line.replace(";", "");
            String[] parts = line.split("=");
            
            if (parts.length == 2) {
                String varName = parts[0].trim();
                String valueStr = parts[1].trim();
                
                if (variables.containsKey(varName)) {
                    Object currentValue = variables.get(varName);
                    String type = getTypeFromValue(currentValue);
                    Object newValue = parseValue(type, valueStr);
                    variables.put(varName, newValue);
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Object parseValue(String type, String valueStr) {
        valueStr = evaluateExpression(valueStr);
        
        switch (type) {
            case "int":
                return Integer.parseInt(valueStr);
            case "double":
                return Double.parseDouble(valueStr);
            case "float":
                return Float.parseFloat(valueStr);
            case "long":
                return Long.parseLong(valueStr);
            case "boolean":
                return Boolean.parseBoolean(valueStr);
            case "String":
                return valueStr.replace("\"", "");
            case "char":
                return valueStr.replace("'", "").charAt(0);
            default:
                return valueStr;
        }
    }
    
    private String getTypeFromValue(Object value) {
        if (value instanceof Integer) return "int";
        if (value instanceof Double) return "double";
        if (value instanceof Float) return "float";
        if (value instanceof Long) return "long";
        if (value instanceof Boolean) return "boolean";
        if (value instanceof String) return "String";
        if (value instanceof Character) return "char";
        return "String";
    }
    
    private String evaluateExpression(String expr) {
        try {
            // Handle string literals first
            if (expr.startsWith("\"") && expr.endsWith("\"")) {
                return expr.substring(1, expr.length() - 1);
            }
            
            // Check if it's a pure numeric expression (no strings involved)
            if (isPureNumericExpression(expr)) {
                return String.valueOf(evaluateNumericExpression(expr));
            }
            
            // Handle string concatenation only if strings are involved
            if (expr.contains("+") && (expr.contains("\"") || hasStringVariable(expr))) {
                return evaluateStringConcatenation(expr);
            }
            
            // Handle other numeric expressions  
            if (expr.matches(".*[+\\-*/].*")) {
                return String.valueOf(evaluateNumericExpression(expr));
            }
            
            // Handle variables
            if (variables.containsKey(expr.trim())) {
                return String.valueOf(variables.get(expr.trim()));
            }
            
            // Return as is if can't evaluate
            return expr;
            
        } catch (Exception e) {
            return expr;
        }
    }
    
    private boolean isPureNumericExpression(String expr) {
        // Remove parentheses and whitespace for analysis
        String cleanExpr = expr.replaceAll("[()\\s]", "");
        
        // Check if expression contains only numbers, operators, and variable names that are numeric
        String[] tokens = cleanExpr.split("[+\\-*/]");
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            // Check if it's a number
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                continue; // It's a number
            }
            
            // Check if it's a numeric variable
            if (variables.containsKey(token) && variables.get(token) instanceof Number) {
                continue; // It's a numeric variable
            }
            
            // Check if it's a string literal or string variable
            if (token.startsWith("\"") || (variables.containsKey(token) && variables.get(token) instanceof String)) {
                return false; // Contains strings, not pure numeric
            }
        }
        
        // If we get here, it's purely numeric
        return cleanExpr.matches(".*[+\\-*/].*"); // Must contain at least one operator
    }
    
    private boolean hasStringVariable(String expr) {
        for (String varName : variables.keySet()) {
            if (expr.contains(varName) && variables.get(varName) instanceof String) {
                return true;
            }
        }
        return false;
    }
    
    private String evaluateStringConcatenation(String expr) {
        StringBuilder result = new StringBuilder();
        String[] parts = expr.split("\\+");
        
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("\"") && part.endsWith("\"")) {
                result.append(part.substring(1, part.length() - 1));
            } else if (variables.containsKey(part)) {
                result.append(variables.get(part));
            } else {
                result.append(part);
            }
        }
        
        return result.toString();
    }
    
    private double evaluateNumericExpression(String expr) {
        try {
            // Replace variables with their numeric values
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    expr = expr.replace(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            
            // Remove all whitespace
            expr = expr.replaceAll("\\s", "");
            
            // Handle parentheses first (recursive evaluation)
            while (expr.contains("(")) {
                int start = expr.lastIndexOf("(");
                int end = expr.indexOf(")", start);
                if (end == -1) break; // Malformed expression
                
                String subExpr = expr.substring(start + 1, end);
                double subResult = evaluateSimpleExpression(subExpr);
                expr = expr.substring(0, start) + subResult + expr.substring(end + 1);
            }
            
            // Evaluate the remaining expression
            return evaluateSimpleExpression(expr);
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double evaluateSimpleExpression(String expr) {
        try {
            // Handle simple case - just a number
            if (expr.matches("-?\\d+(\\.\\d+)?")) {
                return Double.parseDouble(expr);
            }
            
            // Handle multiplication and division first (order of operations)
            expr = evaluateMultiplicationDivision(expr);
            
            // Then handle addition and subtraction
            return evaluateAdditionSubtraction(expr);
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    private String evaluateMultiplicationDivision(String expr) {
        // Handle * and / from left to right
        while (expr.contains("*") || expr.contains("/")) {
            // Find the first * or /
            int mulIndex = expr.indexOf("*");
            int divIndex = expr.indexOf("/");
            
            int opIndex;
            char operator;
            if (mulIndex == -1) {
                opIndex = divIndex;
                operator = '/';
            } else if (divIndex == -1) {
                opIndex = mulIndex;
                operator = '*';
            } else {
                if (mulIndex < divIndex) {
                    opIndex = mulIndex;
                    operator = '*';
                } else {
                    opIndex = divIndex;
                    operator = '/';
                }
            }
            
            // Find the operands
            double left = extractLeftOperand(expr, opIndex);
            double right = extractRightOperand(expr, opIndex);
            
            // Calculate result
            double result = (operator == '*') ? left * right : left / right;
            
            // Replace the expression with the result
            expr = replaceOperation(expr, left, operator, right, result);
        }
        
        return expr;
    }
    
    private double evaluateAdditionSubtraction(String expr) {
        // Handle + and - from left to right
        double result = 0;
        String currentNumber = "";
        char lastOperator = '+';
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            
            if (c == '+' || c == '-') {
                if (!currentNumber.isEmpty()) {
                    double num = Double.parseDouble(currentNumber);
                    result = (lastOperator == '+') ? result + num : result - num;
                    currentNumber = "";
                }
                lastOperator = c;
            } else {
                currentNumber += c;
            }
        }
        
        // Handle the last number
        if (!currentNumber.isEmpty()) {
            double num = Double.parseDouble(currentNumber);
            result = (lastOperator == '+') ? result + num : result - num;
        }
        
        return result;
    }
    
    private double extractLeftOperand(String expr, int opIndex) {
        StringBuilder num = new StringBuilder();
        for (int i = opIndex - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                num.insert(0, c);
            } else if (c == '-' && (i == 0 || "+-*/".indexOf(expr.charAt(i-1)) >= 0)) {
                num.insert(0, c); // Negative number
            } else {
                break;
            }
        }
        return Double.parseDouble(num.toString());
    }
    
    private double extractRightOperand(String expr, int opIndex) {
        StringBuilder num = new StringBuilder();
        boolean isNegative = false;
        
        for (int i = opIndex + 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (i == opIndex + 1 && c == '-') {
                isNegative = true;
            } else if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else {
                break;
            }
        }
        
        double result = Double.parseDouble(num.toString());
        return isNegative ? -result : result;
    }
    
    private String replaceOperation(String expr, double left, char operator, double right, double result) {
        String leftStr = String.valueOf(left);
        String rightStr = String.valueOf(right);
        String operatorStr = String.valueOf(operator);
        
        // Handle negative numbers in the search string
        if (left < 0) leftStr = String.valueOf(left);
        if (right < 0) rightStr = String.valueOf(right);
        
        String searchPattern = leftStr + operatorStr + rightStr;
        String resultStr = String.valueOf(result);
        
        return expr.replace(searchPattern, resultStr);
    }
    
    private boolean handleForLoop(String line) {
        // This would be complex to implement properly
        // For now, just acknowledge it exists
        output.append("// For loop detected (simplified execution)\n");
        return true;
    }
    
    private boolean handleWhileLoop(String line) {
        // This would be complex to implement properly
        // For now, just acknowledge it exists
        output.append("// While loop detected (simplified execution)\n");
        return true;
    }
    
    private boolean handleIfStatement(String line) {
        // This would be complex to implement properly
        // For now, just acknowledge it exists
        output.append("// If statement detected (simplified execution)\n");
        return true;
    }
    
    public static class CompileResult {
        private final boolean success;
        private final String message;
        private final String output;
        
        public CompileResult(boolean success, String message, String output) {
            this.success = success;
            this.message = message;
            this.output = output;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getOutput() {
            return output;
        }
    }
}