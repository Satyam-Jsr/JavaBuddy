package com.example.javabuddy.compiler;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedJavaInterpreter {
    
    private static final String TAG = "AdvancedJavaInterpreter";
    private Context context;
    
    // Runtime environment
    private Map<String, Object> globalVariables;
    private Map<String, JavaClass> classes;
    private Map<String, JavaMethod> methods;
    private Stack<Map<String, Object>> localScopes;
    private Map<String, Object> objectInstances;
    private StringBuilder output;
    private List<String> errors;
    private boolean breakFlag = false;
    private boolean continueFlag = false;
    private Object returnValue = null;
    private boolean returnFlag = false;
    
    public AdvancedJavaInterpreter(Context context) {
        this.context = context;
        this.globalVariables = new HashMap<>();
        this.classes = new HashMap<>();
        this.methods = new HashMap<>();
        this.localScopes = new Stack<>();
        this.objectInstances = new HashMap<>();
        this.output = new StringBuilder();
        this.errors = new ArrayList<>();
    }
    
    public CompileResult compileAndRun(String sourceCode) {
        try {
            // Reset state
            reset();
            
            // Parse the source code
            if (!parseSourceCode(sourceCode)) {
                return new CompileResult(false, "Parse errors:\n" + String.join("\n", errors), "");
            }
            
            // Execute main method
            if (!executeMain()) {
                return new CompileResult(false, "Execution errors:\n" + String.join("\n", errors), output.toString());
            }
            
            return new CompileResult(true, "Execution successful", output.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "Error in compileAndRun", e);
            return new CompileResult(false, "Unexpected error: " + e.getMessage(), output.toString());
        }
    }
    
    private void reset() {
        globalVariables.clear();
        classes.clear();
        methods.clear();
        localScopes.clear();
        objectInstances.clear();
        output.setLength(0);
        errors.clear();
        breakFlag = false;
        continueFlag = false;
        returnValue = null;
        returnFlag = false;
    }
    
    private boolean parseSourceCode(String sourceCode) {
        try {
            // Remove comments
            sourceCode = removeComments(sourceCode);
            
            // Parse classes
            if (!parseClasses(sourceCode)) {
                return false;
            }
            
            // Parse methods
            if (!parseMethods(sourceCode)) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            errors.add("Parse error: " + e.getMessage());
            return false;
        }
    }
    
    private String removeComments(String code) {
        // Remove single-line comments
        code = code.replaceAll("//.*", "");
        
        // Remove multi-line comments
        code = code.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        
        return code;
    }
    
    private boolean parseClasses(String sourceCode) {
        Pattern classPattern = Pattern.compile("(?:public\\s+)?class\\s+(\\w+)(?:\\s+extends\\s+(\\w+))?\\s*\\{([^{}]*(?:\\{[^{}]*\\}[^{}]*)*)\\}");
        Matcher matcher = classPattern.matcher(sourceCode);
        
        while (matcher.find()) {
            String className = matcher.group(1);
            String parentClass = matcher.group(2);
            String classBody = matcher.group(3);
            
            JavaClass javaClass = new JavaClass(className, parentClass);
            
            // Parse class fields and methods
            parseClassMembers(javaClass, classBody);
            
            classes.put(className, javaClass);
        }
        
        return true;
    }
    
    private void parseClassMembers(JavaClass javaClass, String classBody) {
        // Parse fields
        Pattern fieldPattern = Pattern.compile("(?:(public|private|protected)\\s+)?(?:(static)\\s+)?(\\w+)\\s+(\\w+)(?:\\s*=\\s*([^;]+))?\\s*;");
        Matcher fieldMatcher = fieldPattern.matcher(classBody);
        
        while (fieldMatcher.find()) {
            String visibility = fieldMatcher.group(1);
            boolean isStatic = fieldMatcher.group(2) != null;
            String type = fieldMatcher.group(3);
            String name = fieldMatcher.group(4);
            String value = fieldMatcher.group(5);
            
            javaClass.addField(new JavaField(name, type, visibility, isStatic, value));
        }
        
        // Parse methods within class
        Pattern methodPattern = Pattern.compile("(?:(public|private|protected)\\s+)?(?:(static)\\s+)?(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{([^{}]*(?:\\{[^{}]*\\}[^{}]*)*)\\}");
        Matcher methodMatcher = methodPattern.matcher(classBody);
        
        while (methodMatcher.find()) {
            String visibility = methodMatcher.group(1);
            boolean isStatic = methodMatcher.group(2) != null;
            String returnType = methodMatcher.group(3);
            String methodName = methodMatcher.group(4);
            String parameters = methodMatcher.group(5);
            String methodBody = methodMatcher.group(6);
            
            JavaMethod method = new JavaMethod(methodName, returnType, visibility, isStatic, parameters, methodBody);
            javaClass.addMethod(method);
            
            // Also add to global methods for easier access
            methods.put(javaClass.getName() + "." + methodName, method);
        }
    }
    
    private boolean parseMethods(String sourceCode) {
        // Parse standalone methods (outside of classes)
        Pattern methodPattern = Pattern.compile("(?:public\\s+)?(?:static\\s+)?(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{([^{}]*(?:\\{[^{}]*\\}[^{}]*)*)\\}");
        Matcher matcher = methodPattern.matcher(sourceCode);
        
        while (matcher.find()) {
            String returnType = matcher.group(1);
            String methodName = matcher.group(2);
            String parameters = matcher.group(3);
            String methodBody = matcher.group(4);
            
            JavaMethod method = new JavaMethod(methodName, returnType, "public", true, parameters, methodBody);
            methods.put(methodName, method);
        }
        
        return true;
    }
    
    private boolean executeMain() {
        JavaMethod mainMethod = methods.get("main");
        if (mainMethod == null) {
            // Try to find main in a class
            for (JavaClass javaClass : classes.values()) {
                JavaMethod classMain = javaClass.getMethod("main");
                if (classMain != null && classMain.isStatic()) {
                    mainMethod = classMain;
                    break;
                }
            }
        }
        
        if (mainMethod == null) {
            errors.add("No main method found");
            return false;
        }
        
        return executeMethod(mainMethod, new Object[0]);
    }
    
    private boolean executeMethod(JavaMethod method, Object[] args) {
        try {
            // Create local scope
            Map<String, Object> localScope = new HashMap<>();
            localScopes.push(localScope);
            
            // Execute method body
            boolean result = executeBlock(method.getBody());
            
            // Remove local scope
            localScopes.pop();
            
            return result;
            
        } catch (Exception e) {
            errors.add("Method execution error: " + e.getMessage());
            return false;
        }
    }
    
    private boolean executeBlock(String block) {
        String[] lines = block.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (!executeLine(line)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean executeLine(String line) {
        try {
            // Check for control flow flags
            if (breakFlag || continueFlag || returnFlag) {
                return true; // Skip execution if control flow is active
            }
            
            // Handle different statement types
            if (line.contains("System.out.println") || line.contains("System.out.print")) {
                return handlePrintStatement(line);
            } else if (line.matches(".*\\b(int|double|float|long|String|boolean|char)\\s+\\w+.*")) {
                return handleVariableDeclaration(line);
            } else if (line.contains("=") && !line.matches(".*\\b(int|double|float|long|String|boolean|char)\\s+\\w+.*")) {
                return handleAssignment(line);
            } else if (line.trim().startsWith("for")) {
                return handleForLoop(line);
            } else if (line.trim().startsWith("while")) {
                return handleWhileLoop(line);
            } else if (line.trim().startsWith("if")) {
                return handleIfStatement(line);
            } else if (line.trim().equals("break;")) {
                breakFlag = true;
                return true;
            } else if (line.trim().equals("continue;")) {
                continueFlag = true;
                return true;
            } else if (line.contains("++") || line.contains("--")) {
                return handleIncrementDecrement(line);
            }
            
            return true;
            
        } catch (Exception e) {
            errors.add("Error executing line '" + line + "': " + e.getMessage());
            return false;
        }
    }
    
    private boolean handlePrintStatement(String line) {
        Pattern pattern = Pattern.compile("System\\.out\\.(print(?:ln)?)\\s*\\((.+?)\\)\\s*;?");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String method = matcher.group(1);
            String content = matcher.group(2);
            
            String evaluated = evaluateExpression(content);
            
            if ("println".equals(method)) {
                output.append(evaluated).append("\n");
            } else {
                output.append(evaluated);
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean handleVariableDeclaration(String line) {
        line = line.replace(";", "").trim();
        
        Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)(?:\\s*=\\s*(.+))?");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String type = matcher.group(1);
            String varName = matcher.group(2);
            String valueStr = matcher.group(3);
            
            Object value = null;
            if (valueStr != null) {
                value = evaluateExpressionAsValue(valueStr, type);
            } else {
                value = getDefaultValue(type);
            }
            
            setVariable(varName, value);
            return true;
        }
        
        return false;
    }
    
    private boolean handleAssignment(String line) {
        line = line.replace(";", "").trim();
        
        String[] parts = line.split("=", 2);
        if (parts.length == 2) {
            String varName = parts[0].trim();
            String valueStr = parts[1].trim();
            
            Object currentValue = getVariable(varName);
            if (currentValue != null) {
                String type = getTypeFromValue(currentValue);
                Object newValue = evaluateExpressionAsValue(valueStr, type);
                setVariable(varName, newValue);
                return true;
            }
        }
        
        return false;
    }
    
    private boolean handleForLoop(String line) {
        // Simplified for loop handling
        // Pattern: for(init; condition; increment)
        Pattern pattern = Pattern.compile("for\\s*\\(([^;]+);([^;]+);([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String init = matcher.group(1).trim();
            String condition = matcher.group(2).trim();
            String increment = matcher.group(3).trim();
            
            // Execute initialization
            handleVariableDeclaration(init);
            
            // Simple loop execution (limited iterations to prevent infinite loops)
            int maxIterations = 1000;
            int iterations = 0;
            
            while (evaluateCondition(condition) && iterations < maxIterations) {
                // In a real implementation, we'd execute the loop body here
                output.append("// Loop iteration " + (iterations + 1) + "\n");
                
                // Execute increment
                executeLine(increment + ";");
                iterations++;
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean handleWhileLoop(String line) {
        // Simplified while loop
        Pattern pattern = Pattern.compile("while\\s*\\((.+?)\\)");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String condition = matcher.group(1).trim();
            
            int maxIterations = 1000;
            int iterations = 0;
            
            while (evaluateCondition(condition) && iterations < maxIterations) {
                output.append("// While loop iteration " + (iterations + 1) + "\n");
                iterations++;
                
                // In a real implementation, we'd need to modify variables to avoid infinite loop
                break; // For safety, exit after first iteration
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean handleIfStatement(String line) {
        Pattern pattern = Pattern.compile("if\\s*\\((.+?)\\)");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String condition = matcher.group(1).trim();
            boolean result = evaluateCondition(condition);
            
            output.append("// If condition '" + condition + "' evaluated to: " + result + "\n");
            return true;
        }
        
        return false;
    }
    
    private boolean handleIncrementDecrement(String line) {
        line = line.replace(";", "").trim();
        
        if (line.endsWith("++")) {
            String varName = line.substring(0, line.length() - 2);
            Object value = getVariable(varName);
            if (value instanceof Integer) {
                setVariable(varName, (Integer) value + 1);
                return true;
            }
        } else if (line.endsWith("--")) {
            String varName = line.substring(0, line.length() - 2);
            Object value = getVariable(varName);
            if (value instanceof Integer) {
                setVariable(varName, (Integer) value - 1);
                return true;
            }
        }
        
        return false;
    }
    
    private String evaluateExpression(String expr) {
        try {
            Object value = evaluateExpressionAsValue(expr, "String");
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return expr;
        }
    }
    
    private Object evaluateExpressionAsValue(String expr, String expectedType) {
        expr = expr.trim();
        
        // Handle string literals
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return expr.substring(1, expr.length() - 1);
        }
        
        // Handle character literals
        if (expr.startsWith("'") && expr.endsWith("'") && expr.length() == 3) {
            return expr.charAt(1);
        }
        
        // Handle boolean literals
        if ("true".equals(expr) || "false".equals(expr)) {
            return Boolean.parseBoolean(expr);
        }
        
        // Handle numeric literals
        if (expr.matches("-?\\d+")) {
            return Integer.parseInt(expr);
        }
        if (expr.matches("-?\\d*\\.\\d+")) {
            return Double.parseDouble(expr);
        }
        
        // Handle variables
        Object varValue = getVariable(expr);
        if (varValue != null) {
            return varValue;
        }
        
        // Handle parentheses first
        if (expr.contains("(") && expr.contains(")")) {
            return evaluateExpressionWithParentheses(expr, expectedType);
        }
        
        // Check if it's a pure numeric expression (no strings involved)
        if (isPureNumericExpression(expr)) {
            return evaluateArithmetic(expr);
        }
        
        // Handle string concatenation only if strings are involved
        if (expr.contains("+") && (expr.contains("\"") || hasStringVariable(expr))) {
            return evaluateStringConcatenation(expr);
        }
        
        // Handle other arithmetic
        if (expr.matches(".*[+\\-*/].*")) {
            return evaluateArithmetic(expr);
        }
        
        return expr;
    }
    
    private String evaluateStringConcatenation(String expr) {
        StringBuilder result = new StringBuilder();
        String[] parts = expr.split("\\+");
        
        for (String part : parts) {
            part = part.trim();
            Object value = evaluateExpressionAsValue(part, "String");
            result.append(value);
        }
        
        return result.toString();
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
            Object varValue = getVariable(token);
            if (varValue instanceof Number) {
                continue; // It's a numeric variable
            }
            
            // Check if it's a string literal or string variable
            if (token.startsWith("\"") || (varValue instanceof String)) {
                return false; // Contains strings, not pure numeric
            }
        }
        
        // If we get here, it's purely numeric
        return cleanExpr.matches(".*[+\\-*/].*"); // Must contain at least one operator
    }
    
    private boolean hasStringVariable(String expr) {
        for (String varName : getAllVariableNames()) {
            if (expr.contains(varName)) {
                Object value = getVariable(varName);
                if (value instanceof String) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Object evaluateExpressionWithParentheses(String expr, String expectedType) {
        try {
            // Find innermost parentheses
            while (expr.contains("(")) {
                int start = expr.lastIndexOf("(");
                int end = expr.indexOf(")", start);
                if (end == -1) break; // Malformed expression
                
                String subExpr = expr.substring(start + 1, end);
                Object subResult = evaluateExpressionAsValue(subExpr, expectedType);
                expr = expr.substring(0, start) + subResult + expr.substring(end + 1);
            }
            
            // Evaluate the remaining expression
            return evaluateExpressionAsValue(expr, expectedType);
            
        } catch (Exception e) {
            return expr;
        }
    }
    
    private Object evaluateArithmetic(String expr) {
        try {
            // Replace variables with values
            for (String varName : getAllVariableNames()) {
                Object value = getVariable(varName);
                if (value instanceof Number) {
                    expr = expr.replaceAll("\\b" + varName + "\\b", value.toString());
                }
            }
            
            // Remove all whitespace
            expr = expr.replaceAll("\\s", "");
            
            // Handle parentheses recursively
            while (expr.contains("(")) {
                int start = expr.lastIndexOf("(");
                int end = expr.indexOf(")", start);
                if (end == -1) break; // Malformed expression
                
                String subExpr = expr.substring(start + 1, end);
                double subResult = evaluateSimpleArithmetic(subExpr);
                expr = expr.substring(0, start) + subResult + expr.substring(end + 1);
            }
            
            // Evaluate the remaining expression
            double result = evaluateSimpleArithmetic(expr);
            
            // Return as integer if it's a whole number
            if (result == (int) result) {
                return (int) result;
            } else {
                return result;
            }
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double evaluateSimpleArithmetic(String expr) {
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
    
    private boolean evaluateCondition(String condition) {
        // Simple condition evaluation
        condition = condition.trim();
        
        if (condition.contains("==")) {
            String[] parts = condition.split("==");
            if (parts.length == 2) {
                Object left = evaluateExpressionAsValue(parts[0].trim(), "String");
                Object right = evaluateExpressionAsValue(parts[1].trim(), "String");
                return left.equals(right);
            }
        }
        
        if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            if (parts.length == 2) {
                Object left = evaluateExpressionAsValue(parts[0].trim(), "String");
                Object right = evaluateExpressionAsValue(parts[1].trim(), "String");
                return !left.equals(right);
            }
        }
        
        if (condition.contains("<")) {
            String[] parts = condition.split("<");
            if (parts.length == 2) {
                try {
                    double left = Double.parseDouble(evaluateExpressionAsValue(parts[0].trim(), "double").toString());
                    double right = Double.parseDouble(evaluateExpressionAsValue(parts[1].trim(), "double").toString());
                    return left < right;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        
        // Add more condition operators as needed
        
        return false;
    }
    
    private Object getDefaultValue(String type) {
        switch (type) {
            case "int":
            case "short":
            case "byte":
                return 0;
            case "long":
                return 0L;
            case "float":
                return 0.0f;
            case "double":
                return 0.0;
            case "boolean":
                return false;
            case "char":
                return '\0';
            case "String":
                return "";
            default:
                return null;
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
        return "Object";
    }
    
    private void setVariable(String name, Object value) {
        if (!localScopes.isEmpty()) {
            localScopes.peek().put(name, value);
        } else {
            globalVariables.put(name, value);
        }
    }
    
    private Object getVariable(String name) {
        // Check local scopes first (from top to bottom)
        for (int i = localScopes.size() - 1; i >= 0; i--) {
            Map<String, Object> scope = localScopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        
        // Check global variables
        return globalVariables.get(name);
    }
    
    private List<String> getAllVariableNames() {
        List<String> names = new ArrayList<>();
        names.addAll(globalVariables.keySet());
        for (Map<String, Object> scope : localScopes) {
            names.addAll(scope.keySet());
        }
        return names;
    }
    
    // Inner classes for representing Java constructs
    private static class JavaClass {
        private String name;
        private String parentClass;
        private List<JavaField> fields;
        private List<JavaMethod> methods;
        
        public JavaClass(String name, String parentClass) {
            this.name = name;
            this.parentClass = parentClass;
            this.fields = new ArrayList<>();
            this.methods = new ArrayList<>();
        }
        
        public String getName() { return name; }
        public String getParentClass() { return parentClass; }
        
        public void addField(JavaField field) { fields.add(field); }
        public void addMethod(JavaMethod method) { methods.add(method); }
        
        public JavaMethod getMethod(String methodName) {
            for (JavaMethod method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            return null;
        }
    }
    
    private static class JavaField {
        private String name;
        private String type;
        private String visibility;
        private boolean isStatic;
        private String initialValue;
        
        public JavaField(String name, String type, String visibility, boolean isStatic, String initialValue) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
            this.isStatic = isStatic;
            this.initialValue = initialValue;
        }
        
        // Getters
        public String getName() { return name; }
        public String getType() { return type; }
        public String getVisibility() { return visibility; }
        public boolean isStatic() { return isStatic; }
        public String getInitialValue() { return initialValue; }
    }
    
    private static class JavaMethod {
        private String name;
        private String returnType;
        private String visibility;
        private boolean isStatic;
        private String parameters;
        private String body;
        
        public JavaMethod(String name, String returnType, String visibility, boolean isStatic, String parameters, String body) {
            this.name = name;
            this.returnType = returnType;
            this.visibility = visibility;
            this.isStatic = isStatic;
            this.parameters = parameters;
            this.body = body;
        }
        
        // Getters
        public String getName() { return name; }
        public String getReturnType() { return returnType; }
        public String getVisibility() { return visibility; }
        public boolean isStatic() { return isStatic; }
        public String getParameters() { return parameters; }
        public String getBody() { return body; }
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
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getOutput() { return output; }
    }
}